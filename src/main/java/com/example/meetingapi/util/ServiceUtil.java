package com.example.meetingapi.util;

import com.example.meetingapi.entity.Meeting;
import com.example.meetingapi.entity.Schedule;
import com.example.meetingapi.entity.User;
import com.example.meetingapi.repository.MeetingParticipantRepository;
import com.example.meetingapi.repository.MeetingRepository;
import com.example.meetingapi.repository.ScheduleRepository;
import com.example.meetingapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Component
@RequiredArgsConstructor
public class ServiceUtil {

    private final MeetingParticipantRepository meetingParticipantRepository;
    private final MeetingRepository meetingRepository;
    private final UserRepository userRepository;
    private final ScheduleRepository scheduleRepository;

    //편하고자 만듦
    @Transactional(readOnly = true)
    public void meetingParticipationUserCheck(Long meetingId, Long userId) {
        if (!meetingParticipantRepository.existsByMeeting_IdAndUser_Id(meetingId, userId)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"미팅을 참가해야만, 스케쥴을 확인 및 등록할 수 있습니다");
        }
    }

    //id로 meeting 가져오기
    @Transactional(readOnly = true)
    public Meeting findMeetingById(Long meetingId) {

        return meetingRepository.findById(meetingId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.UNAUTHORIZED,"해당 id의 미팅이 존재하지 않습니다. id = " + meetingId)
        );
    }


    //id로 user 가져오기
    @Transactional(readOnly = true)
    public User findUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "해당 id에 일치하는 유저가 없습니다 :: " + userId)
        );
    }


    //id로 schedule 가져오기
    @Transactional(readOnly = true)
    public Schedule findScheduleById(Long scheduleId) {
        return scheduleRepository.findById(scheduleId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.UNAUTHORIZED,"헤당 id의 스케쥴이 존재하지 않습니다. :: id = " + scheduleId)
        );
    }


}
