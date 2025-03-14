package com.example.meetingapi.controller;

import com.example.meetingapi.dto.UserLoginRequestDto;
import com.example.meetingapi.dto.UserRequestDto;
import com.example.meetingapi.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/auth")
public class UserController {

    private final UserService userService;
    //일단 그러면 테스트를 위해서 restcontroller로 테스트 해봐야겠다.


    //회원가입
    @PostMapping("/register")
    @ResponseBody
    public ResponseEntity<?> register(@RequestBody UserRequestDto userRequestDto) {
        userService.registerUser(userRequestDto);
        return ResponseEntity.ok("회원가입 성공");
    }

    //로그인
    @PostMapping("/login")
    @ResponseBody
    public ResponseEntity<?> login(@RequestBody UserLoginRequestDto loginRequestDto
    ) {
        String token = userService.login(loginRequestDto);
        return ResponseEntity.ok().body(Map.of("token", token));
    }


    //로그아웃
    @PostMapping("/logout")
    @ResponseBody
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String token) {
        userService.logout(token);
        return ResponseEntity.ok("로그아웃 성공");
    }
}
