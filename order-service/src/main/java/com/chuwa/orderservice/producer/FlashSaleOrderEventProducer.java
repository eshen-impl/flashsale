package com.chuwa.orderservice.producer;


import com.chuwa.orderservice.payload.FlashSaleOrderResponseEvent;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class FlashSaleOrderEventProducer {
    private final KafkaTemplate<String, FlashSaleOrderResponseEvent> kafkaTemplate;

    public FlashSaleOrderEventProducer(
            @Qualifier("flashSaleOrderEventKafkaTemplate")
            KafkaTemplate<String, FlashSaleOrderResponseEvent> kafkaTemplate
    ) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendToFlashSale(FlashSaleOrderResponseEvent event) {
        kafkaTemplate.send("order-to-flashsale", event);
    }

}
