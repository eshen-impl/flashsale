package com.chuwa.gateway;

import com.chuwa.securitylib.JwtUtil;
import com.chuwa.securitylib.UUIDUtil;
import com.chuwa.securitylib.UserSession;
import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
public class UserHeaderFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        HttpHeaders headers = request.getHeaders();

        // Check if it's a WebSocket upgrade request
        boolean isWebSocket = "websocket".equalsIgnoreCase(headers.getUpgrade());
        log.info("Is Web Socket: {}", isWebSocket);
        if (isWebSocket) {
            // Extract token from query param
            String query = request.getURI().getQuery();
            String token = Optional.ofNullable(query)
                    .flatMap(q -> Arrays.stream(q.split("&"))
                            .filter(p -> p.startsWith("token="))
                            .map(p -> p.substring("token=".length()))
                            .findFirst())
                    .orElse(null);
            log.info("[Web Socket] token: {}", token);
            if (token != null) {
                try {
                    String encodedUserId = JwtUtil.getUserIdFromToken(token);
                    String userId = UUIDUtil.decodeUUID(encodedUserId).toString();
                    log.info("[Web Socket] Injecting User ID into Header: " + userId);
                    ServerHttpRequest modifiedRequest = request.mutate()
                            .header("X-User-Id", userId)
                            .build();

                    return chain.filter(exchange.mutate().request(modifiedRequest).build());
                } catch (JwtException e) {
                    log.warn("[Web Socket] JWT Token invalid: " + e.getMessage());
                    exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                    return exchange.getResponse().setComplete();
                }

            } else {
                log.warn("[Web Socket] JWT Token missing. ");
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }
        }


        // Normal HTTP path: use Spring Security principal
        return exchange.getPrincipal()
                .cast(Authentication.class)
                .flatMap(authentication -> {
                    UserSession userSession = (UserSession) authentication.getPrincipal();
                     if (userSession != null) {
                        String userId = userSession.getUsername();
                        log.info("Injecting User ID into Header: " + userId);

                        // Modify request to include userId in headers
                        ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
                                .header("X-User-Id", userId)
                                .build();

                        return chain.filter(exchange.mutate().request(modifiedRequest).build());
                    } else {
                        return chain.filter(exchange);
                     }
                })
                .switchIfEmpty(chain.filter(exchange));
    }

    @Override
    public int getOrder() {
        return -1; // Ensure it runs early in the filter chain
    }
}
