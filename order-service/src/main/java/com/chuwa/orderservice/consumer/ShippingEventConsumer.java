package com.chuwa.orderservice.consumer;

import com.chuwa.orderservice.payload.ShippingEvent;
import com.chuwa.orderservice.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;


@Component
public class ShippingEventConsumer {
    private static final Logger LOGGER = LoggerFactory.getLogger(ShippingEventConsumer.class);

    private final OrderService orderService;

    public ShippingEventConsumer(OrderService orderService) {
        this.orderService = orderService;
    }

    @KafkaListener(topics = "shipping-to-order", groupId = "order-group",
            containerFactory = "shippingEventListenerFactory")
    public void listenShippingUpdates(ShippingEvent event) {

        try {
            LOGGER.info("Order Service received shipping event: {}", event);
            orderService.processShippingResponse(event);
            LOGGER.info("Shipping event processed successfully for Order ID: {}, type: {}", event.getOrderId(), event.getEventType());
        } catch (Exception e) {
            LOGGER.error("Error deserializing or processing shipping response: {}", e.getMessage(), e);
        }
    }
}
