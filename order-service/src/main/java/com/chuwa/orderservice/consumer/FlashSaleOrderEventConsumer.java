package com.chuwa.orderservice.consumer;

import com.chuwa.orderservice.payload.FlashSaleOrderRequestEvent;
import com.chuwa.orderservice.service.FlashSaleOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class FlashSaleOrderEventConsumer {

    private final FlashSaleOrderService flashSaleOrderService;

    public FlashSaleOrderEventConsumer(FlashSaleOrderService flashSaleOrderService) {
        this.flashSaleOrderService = flashSaleOrderService;
    }


    @KafkaListener(topics = "flashsale-to-order", groupId = "order-group",
            containerFactory = "flashSaleEventListenerFactory")
    public void listenShippingUpdates(FlashSaleOrderRequestEvent event) {

        try {
            log.info("Order Service received flashsale event: {}", event);
            flashSaleOrderService.createOrder(event);
            log.info("Flashsale event processed successfully for Flash Sale ID: {}, User ID: {}", event.getFlashSaleId(), event.getUserId());
        } catch (Exception e) {
            log.error("Error deserializing or processing flashsale response: {}", e.getMessage(), e);
        }
    }
}
