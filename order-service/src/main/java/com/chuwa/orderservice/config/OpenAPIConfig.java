package com.chuwa.orderservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//@Configuration
public class OpenAPIConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Shopping Platform Emart API - Order Service")
                        .version("1.0")
                        .description("Processes customer orders. Manages order creation, updates, cancellations, and status tracking. "
                                + "Publish order event to Kafka for other services and consume payment event from Kafka."))
                    .addSecurityItem(new SecurityRequirement().addList("bearerAuth")) //  Enable Security Globally
                .components(new io.swagger.v3.oas.models.Components()
                        .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")));


    }
}
