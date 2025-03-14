package com.example.meetingapi.service;

import com.example.meetingapi.dto.UserLoginRequestDto;
import com.example.meetingapi.dto.UserRequestDto;
import com.example.meetingapi.entity.User;
import com.example.meetingapi.repository.UserRepository;
import com.example.meetingapi.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
//    private final PasswordEncoder passwordEncoder;


    //회원 가입
    @Transactional
    public User registerUser(UserRequestDto requestDto) {

        if (userRepository.existsByEmail(requestDto.getEmail())) {
            throw new RuntimeException("이미 존재하는 이메일 입니다. 다른 이메일로 회원가입 해주세요");
        }

        //패스워드 인코딩.
//        String encodedPassword = passwordEncoder.encode(requestDto.getPassword());

        User registerUser = new User(
                requestDto.getEmail(),
//                encodedPassword,
                requestDto.getPassword(),
                requestDto.getName()
        );
        return userRepository.save(registerUser);
    }



    //로그인 - jwt토큰 발급
    @Transactional
    public String login(UserLoginRequestDto loginRequestDto) {

        //이메일이 존재하는지 확인
        if (!userRepository.existsByEmail(loginRequestDto.getEmail())) {
            throw new RuntimeException("존재하지 않은 이메일입니다. 이메일을 확인해주세요");
        }

        //이메일이 존재하면, db에서 정보 가져오기.
        User user = userRepository.findByEmail(loginRequestDto.getEmail()).get();

        //비밀번호 확인 후, jwt토큰 발급
        if (loginRequestDto.getPassword().equals(user.getPassword())) {
            return jwtUtil.generateToken(user.getId());
        }

        return null;
    }


    //로그아웃
    @Transactional
    public void logout(String token) {
        jwtUtil.invalidateToken(token);
    }

}
