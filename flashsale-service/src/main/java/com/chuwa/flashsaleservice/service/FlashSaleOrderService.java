package com.chuwa.flashsaleservice.service;

import com.chuwa.flashsaleservice.payload.FlashSaleOrderNotification;

import java.util.UUID;

public interface FlashSaleOrderService {
    FlashSaleOrderNotification submitOrder(Long flashSaleId, UUID userId);


}
