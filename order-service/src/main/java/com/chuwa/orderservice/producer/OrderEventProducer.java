package com.chuwa.orderservice.producer;


import com.chuwa.orderservice.payload.OrderEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class OrderEventProducer {
    private final KafkaTemplate<String, OrderEvent> kafkaTemplate;

    public OrderEventProducer(KafkaTemplate<String, OrderEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendToShipping(OrderEvent event) {
        kafkaTemplate.send("order-to-shipping", event);
    }

}
