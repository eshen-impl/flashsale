package com.chuwa.flashsaleservice.producer;


import com.chuwa.flashsaleservice.payload.FlashSaleOrderRequestEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class FlashSaleOrderEventProducer {
    private final KafkaTemplate<String, FlashSaleOrderRequestEvent> kafkaTemplate;

    public FlashSaleOrderEventProducer(KafkaTemplate<String, FlashSaleOrderRequestEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendToOrder(FlashSaleOrderRequestEvent event) {
        kafkaTemplate.send("flashsale-to-order", event);
    }

}
