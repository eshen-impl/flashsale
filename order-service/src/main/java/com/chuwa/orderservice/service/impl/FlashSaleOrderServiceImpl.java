package com.chuwa.orderservice.service.impl;

import com.chuwa.orderservice.exception.NotOnSaleException;
import com.chuwa.orderservice.exception.ResourceNotFoundException;
import com.chuwa.orderservice.payload.FlashSaleItem;
import com.chuwa.orderservice.payload.OrderDTO;
import com.chuwa.orderservice.service.FlashSaleOrderService;
import com.chuwa.orderservice.util.JsonUtil;
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
    private static final String FLASH_SALE_CACHE_KEY = "flashsale:";


    public FlashSaleOrderServiceImpl(StringRedisTemplate redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public OrderDTO submitOrder(Long flashSaleId, UUID userId) {
        //get item details from redis
        String cachedItem = (String) redisTemplate.opsForHash().get(FLASH_SALE_CACHE_KEY + LocalDate.now(), String.valueOf(flashSaleId));
        FlashSaleItem item = cachedItem != null ? JsonUtil.fromJsonToFlashSaleItem(cachedItem) : null;
        if (item == null) throw new ResourceNotFoundException("Flash Sale item not found!");

        //check if not in sale time window
        if (isItemNotInSaleWindow(item)) throw new NotOnSaleException("This is item is not on sale now! Please come back later!");

        //check if already placed order successfully on the same flash sale item
        // repository -> replace with redis


        return null;
    }

    private boolean isItemNotInSaleWindow(FlashSaleItem item) {
        LocalDateTime saleStart = LocalDateTime.of(item.getSaleDate(), LocalTime.of(item.getSaleStartTime(), 0));
        LocalDateTime saleEnd = LocalDateTime.of(item.getSaleDate(), LocalTime.of(item.getSaleEndTime(), 0));
        LocalDateTime currentTime = LocalDateTime.now();

        return currentTime.isBefore(saleStart) || currentTime.isAfter(saleEnd);
    }
}
