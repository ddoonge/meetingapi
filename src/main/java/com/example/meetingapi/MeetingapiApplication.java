package com.example.meetingapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MeetingapiApplication {

    public static void main(String[] args) {
        SpringApplication.run(MeetingapiApplication.class, args);
    }

//    @Bean
//    public CommandLineRunner run(UserService userService) {
//        return args -> {
//            UserRequestDto userRequestDto = new UserRequestDto();
//            userRequestDto.setEmail("test@test.com");
//            userRequestDto.setName("test");
//            userRequestDto.setPassword("1234");
//            userService.registerUser(userRequestDto);
//            System.out.println("회원가입 되었는데???");
//        };
//    }

}
