package com.chuwa.gateway;

import com.chuwa.securitylib.JwtUtil;
import com.chuwa.securitylib.RedisUserSessionService;
import com.chuwa.securitylib.UserSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import io.jsonwebtoken.JwtException;

@Slf4j
@Component
public class JwtAuthManager implements ReactiveAuthenticationManager {

    private final RedisUserSessionService redisUserSessionService;

    public JwtAuthManager(RedisUserSessionService redisUserSessionService) {
        this.redisUserSessionService = redisUserSessionService;
    }

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        String token = authentication.getCredentials().toString();

        try {
            JwtUtil.validateToken(token); // Validate JWT
            String encodedUserId = JwtUtil.getUserIdFromToken(token);
            log.info("encodedUserId: " + encodedUserId);
            UserSession userSession = redisUserSessionService.getUserSession(encodedUserId);
            log.info("userSession: " + userSession);
            if (userSession == null) {
                return Mono.empty(); // Reject if session is not found
            }

            return Mono.just(new UsernamePasswordAuthenticationToken(userSession, token, userSession.getAuthorities()));
        } catch (JwtException e) {
            return Mono.empty(); // Reject invalid token
        }
    }
}
