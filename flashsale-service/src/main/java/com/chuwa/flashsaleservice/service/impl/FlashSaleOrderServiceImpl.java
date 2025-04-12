package com.chuwa.flashsaleservice.service.impl;

import com.chuwa.flashsaleservice.enums.FlashSaleOrderStatus;
import com.chuwa.flashsaleservice.exception.ExceedPurchaseLimitException;
import com.chuwa.flashsaleservice.exception.InsufficientStockException;
import com.chuwa.flashsaleservice.exception.NotOnSaleException;
import com.chuwa.flashsaleservice.exception.ResourceNotFoundException;
import com.chuwa.flashsaleservice.payload.*;
import com.chuwa.flashsaleservice.producer.FlashSaleOrderEventProducer;
import com.chuwa.flashsaleservice.service.FlashSaleOrderService;
import com.chuwa.flashsaleservice.util.JsonUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Slf4j
@Service
public class FlashSaleOrderServiceImpl implements FlashSaleOrderService {
    private final StringRedisTemplate redisTemplate;
    private final FlashSaleOrderEventProducer flashSaleOrderEventProducer;
    private static final String FLASH_SALE_ITEM_INFO_KEY = "flashsale:item:info:";
    private static final String FLASH_SALE_ITEM_STOCK_KEY = "flashsale:item:stock:";
    private static final String FLASH_SALE_ITEM_ORDER_KEY = "flashsale:item:order:";


    public FlashSaleOrderServiceImpl(StringRedisTemplate redisTemplate, ObjectMapper objectMapper, FlashSaleOrderEventProducer flashSaleOrderEventProducer) {
        this.redisTemplate = redisTemplate;
        this.flashSaleOrderEventProducer = flashSaleOrderEventProducer;
    }

    @Override
    public FlashSaleOrderNotification submitOrder(Long flashSaleId, UUID userId) {
        LocalDate today = LocalDate.now();
        String stockKey = FLASH_SALE_ITEM_STOCK_KEY + today;
        String itemKey = FLASH_SALE_ITEM_INFO_KEY + today;
        String orderKey = FLASH_SALE_ITEM_ORDER_KEY + today;
        String flashSaleIdString = String.valueOf(flashSaleId);
        String userIdString = String.valueOf(userId);

        try {
            //pre check with redis stock to short-circuits early
            String precheckRemain = (String) redisTemplate.opsForHash().get(stockKey, flashSaleIdString);
            if (precheckRemain == null || Long.parseLong(precheckRemain) <= 0) {
                return new FlashSaleOrderNotification(FlashSaleOrderStatus.INSUFFICIENT_STOCK,
                        "Item is out of stock - FlashSaleId: " + flashSaleId);
            }

            //get item details from redis
            String cachedItem = (String) redisTemplate.opsForHash().get(itemKey, flashSaleIdString);
            FlashSaleItem item = cachedItem != null ? JsonUtil.fromJsonToFlashSaleItem(cachedItem) : null;
            if (item == null) {
                return new FlashSaleOrderNotification(FlashSaleOrderStatus.ITEM_NOT_FOUND,
                        "Item not found - FlashSaleId: " + flashSaleId);
            }

            //check if not in sale time window
            if (isItemNotInSaleWindow(item)) {
                return new FlashSaleOrderNotification(FlashSaleOrderStatus.NOT_ON_SALE,
                        "Item not on sale - FlashSaleId: " + flashSaleId);
            }

            //check if successfully purchased number exceeds allowed purchase limit on the same flash sale item
            Long orderCount = redisTemplate.opsForHash().increment(orderKey, userIdString + ":" + flashSaleIdString, 1);
            if (orderCount > item.getPurchaseLimit()) {
                redisTemplate.opsForHash().increment(orderKey, userIdString + ":" + flashSaleIdString, -1);
                return new FlashSaleOrderNotification(FlashSaleOrderStatus.EXCEED_PURCHASE_LIMIT,
                        "User has reached purchase limit of FlashSaleId: " + flashSaleId
                                + ", purchase limit: " + item.getPurchaseLimit());
            }

            //check with redis and pre-deduct stock
            Long remain = redisTemplate.opsForHash().increment(stockKey, flashSaleIdString, -1);
            if (remain < 0)  {
                //rewind order placement count
                redisTemplate.opsForHash().increment(orderKey, userIdString + ":" + flashSaleIdString, -1);
                return new FlashSaleOrderNotification(FlashSaleOrderStatus.INSUFFICIENT_STOCK,
                        "Item is out of stock - FlashSaleId: " + flashSaleId);
            }

            //send flash sale order event to kafka
            flashSaleOrderEventProducer.sendToOrder(convertToFlashSaleOrderEvent(item, userId));
        } catch (Exception e) {
            return new FlashSaleOrderNotification(FlashSaleOrderStatus.FAILED,
                    "Please try again later! Error: " + e.getMessage());
        }

        return new FlashSaleOrderNotification(FlashSaleOrderStatus.PENDING,
                "Confirming order for FlashSaleId: " + flashSaleId);
    }

    private boolean isItemNotInSaleWindow(FlashSaleItem item) {
        LocalDateTime saleStart = LocalDateTime.of(item.getSaleDate(), LocalTime.of(item.getSaleStartTime(), 0));
        LocalDateTime saleEnd = LocalDateTime.of(item.getSaleDate(), LocalTime.of(item.getSaleEndTime(), 0));
        LocalDateTime currentTime = LocalDateTime.now();

        return currentTime.isBefore(saleStart) || currentTime.isAfter(saleEnd);
    }

    private FlashSaleOrderRequestEvent convertToFlashSaleOrderEvent(FlashSaleItem item, UUID userId) {
        return new FlashSaleOrderRequestEvent(item.getFlashSaleId(), userId,
                item.getFlashPrice(), convertToOrderItemJson(item), "USD");
    }

    private String convertToOrderItemJson(FlashSaleItem item) {
        FlashSaleOrderItem orderItem = new FlashSaleOrderItem(item.getItemId(), item.getItemName(), 1, item.getFlashPrice());
        return JsonUtil.toJson(orderItem);
    }
}
