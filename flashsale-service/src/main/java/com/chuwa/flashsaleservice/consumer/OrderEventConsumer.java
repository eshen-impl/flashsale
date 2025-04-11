package com.chuwa.flashsaleservice.consumer;


import com.chuwa.flashsaleservice.payload.FlashSaleOrderResponseEvent;
import com.chuwa.flashsaleservice.websocket.FlashSaleWebSocketHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OrderEventConsumer {

    private final FlashSaleWebSocketHandler flashSaleWebSocketHandler;

    public OrderEventConsumer(FlashSaleWebSocketHandler flashSaleWebSocketHandler) {
        this.flashSaleWebSocketHandler = flashSaleWebSocketHandler;
    }


    @KafkaListener(topics = "order-to-flashsale", groupId = "flashsale-group",
            containerFactory = "orderEventListenerFactory")
    public void listenOrderUpdates(FlashSaleOrderResponseEvent event) {

        try {
            log.info("Flashsale Service received order creation event: {}", event);
            flashSaleWebSocketHandler.sendOrderStatusUpdate(event);
            log.info("Order FlashSaleOrderStatus: {}, Order Id: {}, Message: {} ", event.getStatus(), event.getOrderId(), event.getMessage());
        } catch (Exception e) {
            log.error("Error deserializing or processing order creation response: {}", e.getMessage(), e);
        }
    }
}
