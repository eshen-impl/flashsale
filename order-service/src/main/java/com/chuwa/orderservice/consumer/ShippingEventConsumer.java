package com.chuwa.orderservice.consumer;

import com.chuwa.orderservice.payload.ShippingEvent;
import com.chuwa.orderservice.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ShippingEventConsumer {

    private final OrderService orderService;

    public ShippingEventConsumer(OrderService orderService) {
        this.orderService = orderService;
    }

    @KafkaListener(topics = "shipping-to-order", groupId = "order-group",
            containerFactory = "shippingEventListenerFactory")
    public void listenShippingUpdates(ShippingEvent event) {

        try {
            log.info("Order Service received shipping event: {}", event);
            orderService.processShippingResponse(event);
            log.info("Shipping event processed successfully for Order ID: {}, type: {}", event.getOrderId(), event.getEventType());
        } catch (Exception e) {
            log.error("Error deserializing or processing shipping response: {}", e.getMessage(), e);
        }
    }
}
