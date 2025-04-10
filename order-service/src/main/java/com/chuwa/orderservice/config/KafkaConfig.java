package com.chuwa.orderservice.config;



import com.chuwa.orderservice.payload.*;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.Map;

@Configuration
public class KafkaConfig {
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    private Map<String, Object> producerConfigs() {
        return Map.of(
                "bootstrap.servers", bootstrapServers,
                "key.serializer", StringSerializer.class,
                "value.serializer", JsonSerializer.class
        );
    }

    @Bean(name = "orderEventProducerFactory")
    public ProducerFactory<String, OrderEvent> orderEventProducerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }

    @Bean(name = "orderEventKafkaTemplate")
    public KafkaTemplate<String, OrderEvent> orderEventKafkaTemplate() {
        return new KafkaTemplate<>(orderEventProducerFactory());
    }

    @Bean(name = "flashSaleOrderEventProducerFactory")
    public ProducerFactory<String, FlashSaleOrderResponseEvent> flashSaleOrderEventProducerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }

    @Bean(name = "flashSaleOrderEventKafkaTemplate")
    public KafkaTemplate<String, FlashSaleOrderResponseEvent> flashSaleOrderEventKafkaTemplate() {
        return new KafkaTemplate<>(flashSaleOrderEventProducerFactory());
    }



    private Map<String, Object> consumerConfigs() {
        return Map.of(
                "bootstrap.servers", bootstrapServers,
                "group.id", "order-group",
                "auto.offset.reset", "earliest"
        );
    }

    @Bean(name = "shippingEventConsumerFactory")
    public ConsumerFactory<String, ShippingEvent> shippingEventConsumerFactory() {
        JsonDeserializer<ShippingEvent> deserializer = new JsonDeserializer<>(ShippingEvent.class, false);
        deserializer.addTrustedPackages("*");

        return new DefaultKafkaConsumerFactory<>(
                consumerConfigs(),
                new StringDeserializer(),
                deserializer
        );
    }

    @Bean(name = "shippingEventListenerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, ShippingEvent> shippingEventListenerFactory(
            @Qualifier("shippingEventConsumerFactory") ConsumerFactory<String, ShippingEvent> consumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, ShippingEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        return factory;
    }

    @Bean(name = "paymentEventConsumerFactory")
    public ConsumerFactory<String, PaymentEvent> paymentEventConsumerFactory() {
        JsonDeserializer<PaymentEvent> deserializer = new JsonDeserializer<>(PaymentEvent.class, false);
        deserializer.addTrustedPackages("*");

        return new DefaultKafkaConsumerFactory<>(
                consumerConfigs(),
                new StringDeserializer(),
                deserializer
        );
    }

    @Bean(name = "paymentEventListenerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, PaymentEvent> paymentEventListenerFactory(
            @Qualifier("paymentEventConsumerFactory") ConsumerFactory<String, PaymentEvent> consumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, PaymentEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        return factory;
    }

    @Bean(name = "flashSaleEventConsumerFactory")
    public ConsumerFactory<String, FlashSaleOrderRequestEvent> flashSaleEventConsumerFactory() {
        JsonDeserializer<FlashSaleOrderRequestEvent> deserializer = new JsonDeserializer<>(FlashSaleOrderRequestEvent.class, false);
        deserializer.addTrustedPackages("*");

        return new DefaultKafkaConsumerFactory<>(
                consumerConfigs(),
                new StringDeserializer(),
                deserializer
        );
    }

    @Bean(name = "flashSaleEventListenerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, FlashSaleOrderRequestEvent> flashSaleEventListenerFactory(
            @Qualifier("flashSaleEventConsumerFactory") ConsumerFactory<String, FlashSaleOrderRequestEvent> consumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, FlashSaleOrderRequestEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        return factory;
    }




    @Bean
    public NewTopic orderToShippingTopic() {
        return new NewTopic("order-to-shipping", 3, (short) 1);
    }

    @Bean
    public NewTopic orderToFlashsaleTopic() {
        return new NewTopic("order-to-flashsale", 3, (short) 1);
    }
}


