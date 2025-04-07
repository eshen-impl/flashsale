package com.chuwa.flashsaleservice.service.impl;

import com.chuwa.flashsaleservice.enums.Status;
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
    private final ObjectMapper objectMapper;
    private final FlashSaleOrderEventProducer flashSaleOrderEventProducer;
    private static final String FLASH_SALE_CACHE_KEY = "flashsale:";


    public FlashSaleOrderServiceImpl(StringRedisTemplate redisTemplate, ObjectMapper objectMapper, FlashSaleOrderEventProducer flashSaleOrderEventProducer) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
        this.flashSaleOrderEventProducer = flashSaleOrderEventProducer;
    }

    @Override
    public FlashSaleOrderNotification submitOrder(Long flashSaleId, UUID userId) {
        //get item details from redis
        String cachedItem = (String) redisTemplate.opsForHash().get(FLASH_SALE_CACHE_KEY + LocalDate.now(), String.valueOf(flashSaleId));
        FlashSaleItem item = cachedItem != null ? JsonUtil.fromJsonToFlashSaleItem(cachedItem) : null;
        if (item == null) throw new ResourceNotFoundException("Flash Sale item not found!");

        //check if not in sale time window
        if (isItemNotInSaleWindow(item)) throw new NotOnSaleException("This is item is not on sale now! Please come back later!");

        //check if already placed order successfully on the same flash sale item
        // repository -> replace with redis

        //check with redis and pre-deduct stock

        //send flash sale order event to kafka
        flashSaleOrderEventProducer.sendToOrder(convertToFlashSaleOrderEvent(item, userId));

        return new FlashSaleOrderNotification(Status.PENDING, "Hang tight! We're securing your flash sale item...");
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
