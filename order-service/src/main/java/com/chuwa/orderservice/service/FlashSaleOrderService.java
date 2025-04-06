package com.chuwa.orderservice.service;

import com.chuwa.orderservice.payload.*;

import java.util.UUID;

public interface FlashSaleOrderService {
    OrderDTO submitOrder(Long flashSaleId, UUID userId);


}
