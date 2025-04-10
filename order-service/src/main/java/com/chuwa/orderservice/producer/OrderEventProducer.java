package com.chuwa.orderservice.producer;


import com.chuwa.orderservice.payload.OrderEvent;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class OrderEventProducer {
    private final KafkaTemplate<String, OrderEvent> kafkaTemplate;

    public OrderEventProducer(
            @Qualifier("orderEventKafkaTemplate")
            KafkaTemplate<String, OrderEvent> kafkaTemplate
    ) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendToShipping(OrderEvent event) {
        kafkaTemplate.send("order-to-shipping", event);
    }

}
