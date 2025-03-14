package com.example.meetingapi.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtTokenValidator {

    private final JwtUtil jwtUtil;

    //토큰 검사 메서드
    public Long validToken(String token) {

        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        Long userId = jwtUtil.validateToken(token);
        if (userId == null) {
            throw new RuntimeException("로그인 유지 시간이 지났습니다 다시 로그인 해주세요");
        }
        return userId;
    }
}
