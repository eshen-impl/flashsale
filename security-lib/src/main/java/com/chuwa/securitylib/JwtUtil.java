package com.chuwa.securitylib;


import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;


public class JwtUtil {

//    @Value("${jwt.ttl}")
    private static final Long JWT_TTL = 3600000L; //1 hour in milliseconds

//    @Value("${jwt.key}")
        private static final SecretKey key = Keys.hmacShaKeyFor("your-very-secure-secret-key-must-be-at-least-32-bytes".getBytes(StandardCharsets.UTF_8));

//    private static final SecretKey key = Jwts.SIG.HS512.key().build();

    public static String generateToken(String userId) {

        return Jwts.builder()
                .subject(userId)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + JWT_TTL))
                .signWith(key)
                .compact();
    }

    public static String getUserIdFromToken(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public static void validateToken(String token) {
        Jwts.parser().verifyWith(key).build().parseSignedClaims(token);

    }


}