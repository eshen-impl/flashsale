package com.chuwa.orderservice.service;

import com.chuwa.orderservice.payload.FlashSaleOrderRequestEvent;

import java.util.UUID;

public interface FlashSaleOrderService {
    void createOrder(FlashSaleOrderRequestEvent flashSaleOrderRequestEvent);
    void rollbackRedisStock(Long flashSaleId);
    void rollbackRedisPurchaseHistory(Long flashSaleId, UUID userId);

}
