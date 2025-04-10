package com.chuwa.orderservice.config;

import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NoAuthFeignConfig {
    @Bean
    public RequestInterceptor noopInterceptor() {
        return template -> {
            // No auth header added
        };
    }
}
