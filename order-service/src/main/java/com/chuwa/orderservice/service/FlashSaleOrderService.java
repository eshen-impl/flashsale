package com.chuwa.orderservice.service;

import com.chuwa.orderservice.payload.FlashSaleOrderRequestEvent;

public interface FlashSaleOrderService {
    void createOrder(FlashSaleOrderRequestEvent flashSaleOrderRequestEvent);
}
