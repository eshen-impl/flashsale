package com.chuwa.flashsaleservice.config;

import com.chuwa.flashsaleservice.payload.FlashSaleOrderRequestEvent;
import com.chuwa.flashsaleservice.payload.FlashSaleOrderResponseEvent;
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
    public ProducerFactory<String, FlashSaleOrderRequestEvent> producerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(config);
    }

    @Bean
    public KafkaTemplate<String, FlashSaleOrderRequestEvent> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    private Map<String, Object> consumerConfigs() {
        return Map.of(
                "bootstrap.servers", bootstrapServers,
                "group.id", "flashsale-group",
                "auto.offset.reset", "earliest"
        );
    }

    @Bean(name = "orderEventConsumerFactory")
    public ConsumerFactory<String, FlashSaleOrderResponseEvent> orderEventConsumerFactory() {
        JsonDeserializer<FlashSaleOrderResponseEvent> deserializer = new JsonDeserializer<>(FlashSaleOrderResponseEvent.class, false);
        deserializer.addTrustedPackages("*");

        return new DefaultKafkaConsumerFactory<>(
                consumerConfigs(),
                new StringDeserializer(),
                deserializer
        );
    }


    @Bean(name = "orderEventListenerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, FlashSaleOrderResponseEvent> orderEventListenerFactory(
            @Qualifier("orderEventConsumerFactory") ConsumerFactory<String, FlashSaleOrderResponseEvent> consumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, FlashSaleOrderResponseEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        return factory;
    }


    @Bean
    public NewTopic flashsaleToOrderTopic() {
        return new NewTopic("flashsale-to-order", 3, (short) 1);
    }

}


