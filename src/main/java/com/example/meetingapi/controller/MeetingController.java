package com.example.meetingapi.controller;

import com.example.meetingapi.dto.MeetingDto;
import com.example.meetingapi.dto.ParticipantsResponseDto;
import com.example.meetingapi.entity.Meeting;
import com.example.meetingapi.entity.User;
import com.example.meetingapi.service.MeetingService;
import com.example.meetingapi.util.JwtTokenValidator;
import com.example.meetingapi.util.ServiceUtil;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/meetings")
public class MeetingController {

    private final JwtTokenValidator jwtTokenValidator;
    private final MeetingService meetingService;
    private final ServiceUtil serviceUtil;


    //모임 생성
    @Operation(
            summary = "모임 생성"
    )
    @PostMapping
    public ResponseEntity<?> createMeeting(
            @RequestBody Meeting meeting, @RequestHeader("Authorization") String token) {
        Long userId = jwtTokenValidator.validToken(token);
        meetingService.createMeeting(meeting, userId);

        return ResponseEntity.ok("미팅 생성 완료");
    }


    //모든 모임 목록 조회
    @Operation(
            summary = "모든 모임 목록 조회"
    )
    @GetMapping
    public ResponseEntity<?> viewAllMeetingsList() {
        List<MeetingDto> meetings = meetingService.viewAllMeetingsList();
        return ResponseEntity.ok(meetings);
    }


    // 모임 수정 (생성자만 가능)
    @Operation(
            summary = "모임 수정 (생성자만 가능)"
    )
    @PutMapping("/{meetingId}")
    public ResponseEntity<?> updateMeeting(
            @PathVariable("meetingId") Long meetingId,
            @RequestBody Meeting meeting,
            @RequestHeader("Authorization") String token
    ) {
        //유저 id 가져오기
        Long userId = jwtTokenValidator.validToken(token);
        meetingService.updateMeeting(meetingId, meeting, userId);
        return ResponseEntity.ok("모임 수정 완료");
    }


    //모임 삭제 (생성자만 가능)
    @Operation(
            summary = "모임 삭제 (생성자만 가능)"
    )
    @DeleteMapping("/{meetingId}")
    public ResponseEntity<String> deleteMeeting(
            @PathVariable("meetingId") Long meetingId, @RequestHeader("Authorization") String token
    ) {
        Long userId = jwtTokenValidator.validToken(token);
        User createUser = serviceUtil.findMeetingById(meetingId).getCreateUser();
        Long createUserId = createUser.getId();
        meetingService.deleteMeeting(meetingId, createUserId);

        return ResponseEntity.ok("삭제 완료");
    }


    // 모임 참가
    @Operation(
            summary = "모임 참가"
    )
    @PostMapping("/{meetingId}/join")
    public ResponseEntity<?> meetingParticipation(
            @PathVariable("meetingId") Long meetingId, @RequestHeader("Authorization") String token
    ) {
        Long userId = jwtTokenValidator.validToken(token);
        meetingService.meetingParticipation(meetingId, userId);
        return ResponseEntity.ok("모임 참가 성공");
    }


    //모임 불참 바꾸기
    //그냥 id로 삭제됨... 이러면 안되는데 userid가 아니라...
    @Operation(
            summary = "모임 불참 바꾸기"
    )
    @DeleteMapping("/{meetingId}/withdrawal")
    public ResponseEntity<String> changeMeetingWithdrawal(
            @PathVariable("meetingId") Long meetingId, @RequestHeader("Authorization") String token
    ) {
        Long userId = jwtTokenValidator.validToken(token);
        meetingService.changWithdrawalMeeting(meetingId, userId);
        return ResponseEntity.ok("모임 탈퇴 성공");
    }


    // 모임 참가자 목록 조회
    //오류 발생.
    @Operation(
            summary = "모임 참가자 목록 조회"
    )
    @GetMapping("/{meetingId}/participants")
    public ResponseEntity<?> viewMeetingParticipants(
            @PathVariable("meetingId") Long meetingId,
            @RequestHeader("Authorization") String token
    ) {
        jwtTokenValidator.validToken(token);
        List<ParticipantsResponseDto> participantsResponseDtos = meetingService.viewMeetingParticipants(meetingId);
        return ResponseEntity.ok(participantsResponseDtos);
    }
}





