package com.example.meetingapi.controller;

import com.example.meetingapi.dto.ParticipantsResponseDto;
import com.example.meetingapi.dto.ScheduleDto;
import com.example.meetingapi.service.ScheduleService;
import com.example.meetingapi.util.JwtTokenValidator;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/meeting/{meetingId}/schedules")
public class ScheduleController {

    private final ScheduleService scheduleService;
    private final JwtTokenValidator jwtTokenValidator;

    //스케쥴 생성
    @Operation(
            summary = "스케쥴 생성"
    )
    @PostMapping
    public ResponseEntity<String> createSchedule(
            @PathVariable("meetingId") Long meetingId,
            @RequestBody ScheduleDto scheduleDto, @RequestHeader("Authorization") String token
    ) {
        Long userId = jwtTokenValidator.validToken(token);
        scheduleService.createSchedule(userId, meetingId, scheduleDto);
        return ResponseEntity.ok("스케쥴 추가 성공");
    }


    //스케쥴 목록 조회
    @Operation(
            summary = "스케쥴 목록 조회"
    )
    @GetMapping
    public ResponseEntity<?> viewScheduleList(
            @PathVariable("meetingId") Long meetingId, @RequestHeader("Authorization") String token) {
        Long userId = jwtTokenValidator.validToken(token);
        List<ScheduleDto> scheduleDtos = scheduleService.viewScheduleList(meetingId, userId);
        return ResponseEntity.ok(scheduleDtos);
    }


    //스케쥴 참가
    @Operation(
            summary = "스케쥴 참가"
    )
    @PostMapping("/{scheduleId}/join")
    public ResponseEntity<?> joinSchedule(
            @PathVariable("meetingId") Long meetingId,
            @PathVariable("scheduleId") Long scheduleId,
            @RequestHeader("Authorization") String token
    ) {
        Long userId = jwtTokenValidator.validToken(token);
        scheduleService.joinSchedule(meetingId, userId, scheduleId);
        return ResponseEntity.ok("스케쥴 참가 완료");
    }



    //스케쥴 참가 취소
    @Operation(
            summary = "스케쥴 참가 취소"
    )
    @PostMapping("/{scheduleId}/withdraw")
    public ResponseEntity<?> withdrawSchedule(
            @PathVariable("meetingId") Long meetingId,
            @PathVariable("scheduleId") Long scheduleId,
            @RequestHeader("Authorization") String token
    ) {
        Long userId = jwtTokenValidator.validToken(token);
        scheduleService.withdrawSchedule(meetingId, userId, scheduleId);
        return ResponseEntity.ok("스케쥴 참여 취소 완료");
    }


    //스케쥴 삭제
    @Operation(
            summary = "스케쥴 삭제"
    )
    @DeleteMapping("/{scheduleId}/delete")
    public ResponseEntity<?> deleteSchedule(
            @PathVariable("meetingId") Long meetingId,
            @PathVariable("scheduleId") Long scheduleId,
            @RequestHeader("Authorization") String token
    ) {
        Long userId = jwtTokenValidator.validToken(token);
        scheduleService.deleteSchedule(meetingId, userId, scheduleId);
        return ResponseEntity.ok("스케쥴 삭제 완료");
    }


    //특정 스케쥴 참가자 목록 조회
    @Operation(
            summary = "특정 스케쥴 참가자 목록 조회"
    )
    @GetMapping("/{scheduleId}/participants")
    public ResponseEntity<?>  viewScheduleParticipants(
            @PathVariable("meetingId") Long meetingId,
            @PathVariable("scheduleId") Long scheduleId,
            @RequestHeader("Authorization") String token
    ) {
        Long userId = jwtTokenValidator.validToken(token);
        List<ParticipantsResponseDto> participantsResponseDto =
                scheduleService.viewScheduleParticipants(meetingId, userId, scheduleId);

        return ResponseEntity.ok(participantsResponseDto);
    }
}
