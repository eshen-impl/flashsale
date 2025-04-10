package com.chuwa.orderservice.config;

import com.chuwa.orderservice.interceptor.FeignClientInterceptor;
import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SecuredFeignConfig {
    @Bean
    public RequestInterceptor securedFeignClientInterceptor() {
        return new FeignClientInterceptor();
    }
}

