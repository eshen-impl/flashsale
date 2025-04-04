package com.chuwa.securitylib;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RedisUserSessionService {
    private final StringRedisTemplate redisTemplate;
    private static final ObjectMapper objectMapper = CustomObjectMapperProvider.create();

    private static final String LOGIN_CACHE_KEY = "login:";

    public RedisUserSessionService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;

    }

    public void saveUserSession(String userId, UserSession session, long expiration) {
        try {
            String value = objectMapper.writeValueAsString(session);
            redisTemplate.opsForValue().set(LOGIN_CACHE_KEY + userId, value, expiration, TimeUnit.MINUTES);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize user session", e);
        }

    }

    public UserSession getUserSession(String userId)  {
        String json = redisTemplate.opsForValue().get(LOGIN_CACHE_KEY + userId);
        try {
            return json != null ? objectMapper.readValue(json, UserSession.class) : null;
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to deserialize user session", e);
        }
    }

    public void deleteUserSession(String userId) {
        redisTemplate.delete(LOGIN_CACHE_KEY + userId);
    }
}
