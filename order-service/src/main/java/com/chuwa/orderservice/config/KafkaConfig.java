package com.chuwa.orderservice.config;



import com.chuwa.orderservice.payload.FlashSaleOrderRequestEvent;
import com.chuwa.orderservice.payload.OrderEvent;
import com.chuwa.orderservice.payload.PaymentEvent;
import com.chuwa.orderservice.payload.ShippingEvent;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
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

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Bean
    public ProducerFactory<String, OrderEvent> producerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(config);
    }

    @Bean
    public KafkaTemplate<String, OrderEvent> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    private Map<String, Object> consumerConfigs() {
        return Map.of(
                "bootstrap.servers", bootstrapServers,
                "group.id", "order-group",
                "auto.offset.reset", "earliest"
        );
    }

    @Bean
    @Qualifier("shippingEventConsumerFactory")
    public ConsumerFactory<String, ShippingEvent> shippingEventConsumerFactory() {
        JsonDeserializer<ShippingEvent> deserializer = new JsonDeserializer<>(ShippingEvent.class, false);
        deserializer.addTrustedPackages("*");

        return new DefaultKafkaConsumerFactory<>(
                consumerConfigs(),
                new StringDeserializer(),
                deserializer
        );
    }

    @Bean
    @Qualifier("shippingEventListenerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, ShippingEvent> shippingEventListenerFactory(
            @Qualifier("shippingEventConsumerFactory") ConsumerFactory<String, ShippingEvent> consumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, ShippingEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        return factory;
    }

    @Bean
    @Qualifier("paymentEventConsumerFactory")
    public ConsumerFactory<String, PaymentEvent> paymentEventConsumerFactory() {
        JsonDeserializer<PaymentEvent> deserializer = new JsonDeserializer<>(PaymentEvent.class, false);
        deserializer.addTrustedPackages("*");

        return new DefaultKafkaConsumerFactory<>(
                consumerConfigs(),
                new StringDeserializer(),
                deserializer
        );
    }

    @Bean
    @Qualifier("paymentEventListenerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, PaymentEvent> paymentEventListenerFactory(
            @Qualifier("paymentEventConsumerFactory") ConsumerFactory<String, PaymentEvent> consumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, PaymentEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        return factory;
    }

    @Bean
    @Qualifier("flashsaleEventConsumerFactory")
    public ConsumerFactory<String, FlashSaleOrderRequestEvent> flashsaleEventConsumerFactory() {
        JsonDeserializer<FlashSaleOrderRequestEvent> deserializer = new JsonDeserializer<>(FlashSaleOrderRequestEvent.class, false);
        deserializer.addTrustedPackages("*");

        return new DefaultKafkaConsumerFactory<>(
                consumerConfigs(),
                new StringDeserializer(),
                deserializer
        );
    }

    @Bean
    @Qualifier("flashsaleEventListenerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, FlashSaleOrderRequestEvent> flashsaleEventListenerFactory(
            @Qualifier("flashsaleEventConsumerFactory") ConsumerFactory<String, FlashSaleOrderRequestEvent> consumerFactory) {
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


