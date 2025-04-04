package com.chuwa.securitylib;


import org.springframework.stereotype.Service;

@Service
public class RedisUserSessionService {
    private final RedisUtil redisUtil;

    public RedisUserSessionService(RedisUtil redisUtil) {
        this.redisUtil = redisUtil;
    }

    public void saveUserSession(String userId, UserSession session, long expiration) {
        redisUtil.save("login:" + userId, session, expiration);
    }

    public UserSession getUserSession(String userId) {
        return (UserSession) redisUtil.get("login:" + userId);
    }

    public void deleteUserSession(String userId) {
        redisUtil.delete("login:" + userId);
    }
}
