package com.chuwa.flashsaleservice.config;

import com.chuwa.flashsaleservice.websocket.FlashSaleWebSocketHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final FlashSaleWebSocketHandler webSocketHandler;

    public WebSocketConfig(FlashSaleWebSocketHandler webSocketHandler) {
        this.webSocketHandler = webSocketHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(webSocketHandler, "/ws/flashsale/order-updates")
                .setAllowedOrigins("*"); // for CORS support
//                .withSockJS(); //SockJS fallback support
    }
}

