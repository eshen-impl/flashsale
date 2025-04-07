package com.chuwa.flashsaleservice.consumer;


import com.chuwa.flashsaleservice.payload.FlashSaleOrderResponseEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OrderEventConsumer {

//    private final OrderService orderService;

//    public OrderEventConsumer(OrderService orderService) {
//        this.orderService = orderService;
//    }

    @KafkaListener(topics = "order-to-flashsale", groupId = "flashsale-group",
            containerFactory = "orderEventListenerFactory")
    public void listenOrderUpdates(FlashSaleOrderResponseEvent event) {

        try {
            log.info("Flashsale Service received order creation event: {}", event);
//            orderService.processPaymentResponse(event); hand to websocket
            log.info("Order Status: {}, Order Id: {} ", event.getStatus(), event.getOrderId());
        } catch (Exception e) {
            log.error("Error deserializing or processing order creation response: {}", e.getMessage(), e);
        }
    }
}
