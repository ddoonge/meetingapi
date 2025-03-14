package com.example.meetingapi.service;

import com.example.meetingapi.dto.ParticipantsResponseDto;
import com.example.meetingapi.dto.ScheduleDto;
import com.example.meetingapi.entity.*;
import com.example.meetingapi.repository.MeetingRepository;
import com.example.meetingapi.repository.ScheduleParticipantRepository;
import com.example.meetingapi.repository.ScheduleRepository;
import com.example.meetingapi.repository.UserRepository;
import com.example.meetingapi.util.ServiceUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final UserRepository userRepository;
    private final MeetingRepository meetingRepository;
    private final ServiceUtil serviceUtil;
    private final ScheduleParticipantRepository scheduleParticipantRepository;


    //스케쥴 생성
    @Transactional
    public Schedule createSchedule(Long userId, Long meetingId, ScheduleDto scheduleDto) {
        if (!meetingRepository.existsById(meetingId)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"현재 미팅 id가 존재 하지 않습니다.");
        }
        if (!userRepository.existsById(userId)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"현재 userId가 존재 하지 않는다");
        }

        //유저가 미팅에 속한지 확인
        serviceUtil.meetingParticipationUserCheck(meetingId, userId);


        User user = serviceUtil.findUserById(userId);
        Meeting meeting = serviceUtil.findMeetingById(meetingId);

        Schedule newSchedule = Schedule.builder()
                .date(scheduleDto.getDate())
                .location(scheduleDto.getLocation())
                .title(scheduleDto.getTitle())
                .time(scheduleDto.getTime())
                .createUser(user)
                .meeting(meeting)
                .scheduleParticipants(new ArrayList<>())
                .build();

        ScheduleParticipant scheduleParticipant =
                new ScheduleParticipant(user, newSchedule, ScheduleState.ATTENDING);

        //스케쥴을 통한, 스케쥴 참여자 db에 추가하기.
        newSchedule.getScheduleParticipants().add(scheduleParticipant);

        //미팅을 통해, 스케쥴 찾을 수 있도록 추가하기
        meeting.getSchedules().add(newSchedule);

        return scheduleRepository.save(newSchedule);
    }


    //스케쥴 목록 조회( 모임에 참가하면 누구나 가능)
    @Transactional(readOnly = true)
    public List<ScheduleDto> viewScheduleList(Long meetingId, Long userId) {
        //유저가 모임에 참가한지 확인.
        serviceUtil.meetingParticipationUserCheck(meetingId, userId);

        Meeting meeting = serviceUtil.findMeetingById(meetingId);

        List<Schedule> schedules = meeting.getSchedules();
        List<ScheduleDto> scheduleDtos = new ArrayList<>();

        for (Schedule schedule : schedules) {
            ScheduleDto scheduleDto = ScheduleDto.builder()
                    .id(schedule.getId())
                    .title(schedule.getTitle())
                    .date(schedule.getDate())
                    .time(schedule.getTime())
                    .location(schedule.getLocation())
                    .build();

            scheduleDtos.add(scheduleDto);
        }

        return scheduleDtos;
    }


    //스케쥴 참가
    @Transactional
    public ScheduleParticipant joinSchedule(Long meetingId, Long userId, Long scheduleId) {

        //먼저 유저가 미팅에 참여한지 확인하기
        serviceUtil.meetingParticipationUserCheck(meetingId, userId);

        //스케쥴 있는지 찾아보고
        Schedule schedule = serviceUtil.findScheduleById(scheduleId);

        //스케쥴과 미팅이 같은 미팅에 있는지 확인하고
        if (!schedule.getMeeting().getId().equals(meetingId)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "해당 모임에 현재 스케쥴이 있지 않습니다! ");
        }

        //이미 참여중인 사용자라면, 에러 처리
        if (scheduleParticipantRepository
                .existsBySchedule_IdAndUser_IdAndUserState(
                        scheduleId, userId, ScheduleState.ATTENDING )){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"이미 참여하고 있습니다");
        }

        //유저가 스케쥴 참가하도록 db에 설정하기
        ScheduleParticipant scheduleParticipant = new ScheduleParticipant();

        //1. 만약 이미db에 있다면, 다시 참가로 바꾸기
        if (scheduleParticipantRepository.existsBySchedule_IdAndUser_Id(scheduleId, userId)) {
            scheduleParticipant = scheduleParticipantRepository.findBySchedule_IdAndUser_Id(scheduleId, userId).get();
            scheduleParticipant.setUserState(ScheduleState.ATTENDING);
        } else {
            //2. 처음이라면, 디비 생성후에 참가로 넣기
            User user = serviceUtil.findUserById(userId);
            scheduleParticipant.setSchedule(schedule);
            scheduleParticipant.setUser(user);
            scheduleParticipant.setUserState(ScheduleState.ATTENDING);
            schedule.getScheduleParticipants().add(scheduleParticipant);
        }

        return scheduleParticipantRepository.save(scheduleParticipant);
    }


    //스케쥴 탈퇴
    @Transactional
    public void withdrawSchedule(Long meetingId, Long userId, Long scheduleId) {
        //스케쥴을 일부러 삭제하는 것보다는 그냥 남겨두는게 추억으로 의미 있지 않을까?
        //진짜 삭제 기능도 따로 만들자.

        //먼저 유저가 미팅에 참여한지 확인하기
        serviceUtil.meetingParticipationUserCheck(meetingId, userId);

        //스케쥴 있는지 찾아보고
        Schedule schedule = serviceUtil.findScheduleById(scheduleId);

        //스케쥴과 미팅이 같은 미팅에 있는지 확인하고
        if (!schedule.getMeeting().getId().equals(meetingId)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "해당 모임에 현재 스케쥴이 있지 않습니다! ");
        }

        //schedule 참가자를 통해서 상태 변화를 시키면 될듯.
        ScheduleParticipant scheduleParticipant = scheduleParticipantRepository.findBySchedule_IdAndUser_Id(scheduleId, userId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "현재 스케쥴에 참가중이지 않습니다!")
        );
        scheduleParticipant.setUserState(ScheduleState.NOT_ATTENDING);

    }


    //스케쥴 삭제( 생성자만 가능)
    @Transactional
    public void deleteSchedule(Long meetingId, Long userId, Long scheduleId) {
        //먼저 유저가 미팅에 참여한지 확인하기
        serviceUtil.meetingParticipationUserCheck(meetingId, userId);

        //스케쥴 있는지 찾아보고
        Schedule schedule = serviceUtil.findScheduleById(scheduleId);

        //스케쥴과 미팅이 같은 미팅에 있는지 확인하고
        if (!schedule.getMeeting().getId().equals(meetingId)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "해당 모임에 현재 스케쥴이 있지 않습니다! ");
        }

        //생성자 id랑 로그인한 id랑 같으면 되잖아!
        if (!schedule.getCreateUser().getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "현재 스케쥴의 생성자가 아닙니다!");
        }
        scheduleRepository.delete(schedule);
    }


    //특정 스케쥴 참가자 목록 조회
    @Transactional(readOnly = true)
    public List<ParticipantsResponseDto> viewScheduleParticipants(Long meetingId, Long userId, Long scheduleId) {
        //먼저 유저가 미팅에 참여한지 확인하기
        serviceUtil.meetingParticipationUserCheck(meetingId, userId);

        //스케쥴 있는지 찾아보고
        Schedule schedule = serviceUtil.findScheduleById(scheduleId);

        //스케쥴과 미팅이 같은 미팅에 있는지 확인하고
        if (!schedule.getMeeting().getId().equals(meetingId)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "해당 모임에 현재 스케쥴이 있지 않습니다! ");
        }

        //그러면 유저가 스케쥴에 참가한지 확인
        if (!scheduleParticipantRepository.existsBySchedule_IdAndUser_IdAndUserState(scheduleId, userId, ScheduleState.ATTENDING)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "해당 모임을 참가 하지 않아서 볼 수 없습니다");
        }


        //이제부터 본격적으로 목록 찾기
        //추가로 상태가 참가로 되어 있는 유저들 찾기
        List<ScheduleParticipant> scheduleParticipants = scheduleParticipantRepository.findBySchedule_IdAndUserState(scheduleId, ScheduleState.ATTENDING);


        //이제 dto에 담아주기
        List<ParticipantsResponseDto> responseDtos = new ArrayList<>();

        for (ScheduleParticipant participant : scheduleParticipants) {
            ParticipantsResponseDto responseDto = new ParticipantsResponseDto();
            responseDto.setId(participant.getUser().getId());
            responseDto.setName(participant.getUser().getName());

            responseDtos.add(responseDto);
        }

        return responseDtos;

    }

}
