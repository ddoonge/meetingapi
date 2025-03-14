package com.example.meetingapi.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.stereotype.Component;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;


@Component
public class JwtUtil {
    private static final String SECRET = "my-secret-key";
    private static final long EXPIRATION_TIME = 1000 * 60 * 60;
    private final ConcurrentHashMap<String, Boolean> invalidTokens = new ConcurrentHashMap<>();

    public String generateToken(Long userId) {
        return JWT.create()
                .withSubject(String.valueOf(userId))
                .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .sign(Algorithm.HMAC256(SECRET));
    }

    public Long validateToken(String token) {
        try {
            if (invalidTokens.containsKey(token)) {
                return null;
            }
            DecodedJWT decodedJWT = JWT.require(Algorithm.HMAC256(SECRET)).build().verify(token);
            return Long.parseLong(decodedJWT.getSubject());
        } catch (JWTVerificationException | NumberFormatException e) {
            return null;
        }
    }

    public void invalidateToken(String token) {
        invalidTokens.put(token, true);
    }


}