package com.chuwa.flashsaleservice.websocket;

import com.chuwa.flashsaleservice.payload.FlashSaleOrderResponseEvent;
import com.chuwa.flashsaleservice.util.JsonUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class FlashSaleWebSocketHandler extends TextWebSocketHandler {
    private final Map<String, WebSocketSession> sessionMap = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String userId = getUserIdFromHeaders(session);
        if (userId != null) {
            sessionMap.put(userId, session);
            log.info("WebSocket connected for userId: {}", userId);
        } else {
            log.warn("Missing X-User-Id in WebSocket headers");
            session.close(CloseStatus.BAD_DATA);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String userId = getUserIdFromHeaders(session);
        if (userId != null) {
            sessionMap.remove(userId);
            log.info("WebSocket closed for userId: {}", userId);
        }
    }

    public void sendOrderStatusUpdate(FlashSaleOrderResponseEvent event) {
        String userId = event.getUserId().toString();
        String payload = JsonUtil.toJson(event);

        int attempts = 10;
        long delay = 500; // ms

        for (int i = 0; i < attempts; i++) {
            WebSocketSession session = sessionMap.get(event.getUserId().toString());
            if (session != null && session.isOpen()) {
                try {
                    session.sendMessage(new TextMessage(payload));
                    return;
                } catch (Exception e) {
                    log.error("Error sending message via WebSocket to user {}: {}", userId, e.getMessage());
                }
            } else {
                log.warn("WebSocket session not open for user: {}", userId);
            }

            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private String getUserIdFromHeaders(WebSocketSession session) {
        List<String> userIdHeaders = session.getHandshakeHeaders().get("X-User-Id");
        return (userIdHeaders != null && !userIdHeaders.isEmpty()) ? userIdHeaders.get(0) : null;
    }
}
