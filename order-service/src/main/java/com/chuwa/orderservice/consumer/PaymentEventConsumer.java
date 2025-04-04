package com.chuwa.orderservice.consumer;


import com.chuwa.orderservice.payload.PaymentEvent;

import com.chuwa.orderservice.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;


@Component
public class PaymentEventConsumer {
    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentEventConsumer.class);

    private final OrderService orderService;

    public PaymentEventConsumer(OrderService orderService) {
        this.orderService = orderService;
    }

    @KafkaListener(topics = "payment-to-order", groupId = "order-group",
            containerFactory = "paymentEventListenerFactory")
    public void listenPaymentUpdates(PaymentEvent event) {

        try {
            LOGGER.info("Order Service received payment event: {}", event);
            orderService.processPaymentResponse(event);
            LOGGER.info("Payment event processed successfully for Order ID: {}, type: {}", event.getOrderId(), event.getEventType());
        } catch (Exception e) {
            LOGGER.error("Error deserializing or processing payment response: {}", e.getMessage(), e);
        }
    }
}
