package com.example.meetingapi.service;

import com.example.meetingapi.dto.MeetingDto;
import com.example.meetingapi.dto.ParticipantsResponseDto;
import com.example.meetingapi.entity.Meeting;
import com.example.meetingapi.entity.MeetingParticipant;
import com.example.meetingapi.entity.User;
import com.example.meetingapi.repository.MeetingParticipantRepository;
import com.example.meetingapi.repository.MeetingRepository;
import com.example.meetingapi.util.ServiceUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MeetingService {

    private final MeetingRepository meetingRepository;
    private final MeetingParticipantRepository meetingParticipantRepository;
    private final ServiceUtil serviceUtil;


    //모든 모임 목록 조회
    @Transactional(readOnly = true)
    public List<MeetingDto> viewAllMeetingsList() {
        List<Meeting> meetings = meetingRepository.findAll();
        List<MeetingDto> meetingDtos = new ArrayList<>();

        for (Meeting meeting : meetings) {
            MeetingDto meetingDto = new MeetingDto();
            meetingDto.setId(meeting.getId());
            meetingDto.setName(meeting.getName());
            meetingDto.setDescription(meeting.getDescription());
            meetingDto.setMaxParticipants(meeting.getMaxParticipants());
            meetingDto.setCurrentParticipants(meeting.getCurrentParticipants());
            //dto리스트에 dto 객체를 추가
            meetingDtos.add(meetingDto);
        }
        return meetingDtos;
    }



    //모임 생성(유저 정보도 들어와야됨. 그리고 유저 참여 테이블도 같이 생성되야된다 )
    @Transactional
    public Meeting createMeeting(Meeting meeting, Long userId) {


        Meeting newMeeting = new Meeting();
        newMeeting.setName(meeting.getName());
        newMeeting.setDescription(meeting.getDescription());
        newMeeting.setMaxParticipants(meeting.getMaxParticipants());
        newMeeting.setCurrentParticipants(1);
        //모임 생성자 찾고 db 저장하기
        User createUser = serviceUtil.findUserById(userId);
        newMeeting.setCreateUser(createUser);

        //모임참가자 생성자 생성.
        MeetingParticipant meetingParticipant =
                new MeetingParticipant(createUser, newMeeting);


        //cascade로 설정해서 하나만 설정하면, 둘다 들어감!
        //이게 꼭 필요하다.
        newMeeting.getParticipants().add(meetingParticipant);

        return meetingRepository.save(newMeeting);
    }




    //모임 수정(생성자만 가능)
    @Transactional
    public Meeting updateMeeting(Long meetingId, Meeting meeting, Long createUserId) {
        if (!meetingRepository.existsById(meetingId)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"해당 미팅id가 존재하지 않습니다. :: 미팅 id = " + meetingId);
        }
        Meeting updateMeeting = serviceUtil.findMeetingById(meetingId);

        if (!updateMeeting.getCreateUser().getId().equals(createUserId)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"meeting을 생성한 사람만 수정할 수 있습니다.");
        }
        updateMeeting.setDescription(meeting.getDescription());
        updateMeeting.setName(meeting.getName());
        updateMeeting.setMaxParticipants(meeting.getMaxParticipants());

        return meetingRepository.save(updateMeeting);
    }




    //모임 삭제( 생성자만 삭제 가능, 모임창가자 테이블도 고려 해야됨 )
    @Transactional
    public void deleteMeeting(Long meetingId, Long createUserId) {
        Meeting meeting = serviceUtil.findMeetingById(meetingId);

        //미팅에 참여자가 0명일때, 삭제 되는 로직
        int currentParticipants = meeting.getCurrentParticipants();
        if (currentParticipants == 0) {
            meetingRepository.delete(meeting);
        }

        //미팅에 참여자가 있음에도 불구하고, 삭재하는 로직(연관 테이블때문에 참여하는 모든 유저를 다 제거해야된다)
        //먼저 미팅의 생성자인지 확인
        boolean isCreater = createUserId.equals(meeting.getCreateUser().getId());
        if (isCreater) {
            List<MeetingParticipant> meetingParticipants = meetingParticipantRepository.findByMeeting_Id(meetingId).orElseThrow();
            meetingParticipantRepository.deleteAll(meetingParticipants);
            meetingRepository.delete(meeting);
        } else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"미팅을 삭제하기 위해서는 미팅의 생성자여야 가능합니다");
        }

        //생성자가 맞다고 확인되면, 미팅에 참가하는 모든 유저 제거.


    }




    //모임 참가
    @Transactional
    public Meeting meetingParticipation(Long meetingId, Long userId) {

        if (!meetingRepository.existsById(meetingId)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"존재하지 않는 미팅id 입니다 :: id = " + meetingId);
        }
        if (meetingParticipantRepository.existsByMeeting_IdAndUser_Id(meetingId, userId)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"이미 참여하고 있는 미팅입니다.");
        }
        Meeting meeting = serviceUtil.findMeetingById(meetingId);

        int currentParticipants = meeting.getCurrentParticipants();
        if ((currentParticipants + 1) > meeting.getMaxParticipants()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"이미 인원이 꽉찬 모임입니다. ::  최대 모임인원 수 ::" + meeting.getMaxParticipants());
        }

        // 현재 참여자 수 업데이트
        meeting.setCurrentParticipants(meeting.getCurrentParticipants() + 1);

        //미팅 참여자 추가.
        User user = serviceUtil.findUserById(userId);
        MeetingParticipant meetingParticipant = new MeetingParticipant(user, meeting);
        meeting.getParticipants().add(meetingParticipant);

        return meetingRepository.save(meeting);
    }




    //모임 불참
    @Transactional
    public void changWithdrawalMeeting(Long meetingId, Long userId) {

        if (!meetingParticipantRepository.existsByMeeting_IdAndUser_Id(meetingId,userId)) {
            throw new RuntimeException("해당 미팅에 참가하는 user가 아닙니다!");
        }

        //cascade가 이게 안되니깐, 왜냐하면 자손부터 손보니깐 적용 x
        //그래서 meeting도 db에서 가져오고, 이것을 요리조리 만져야된다.
        Meeting meeting = serviceUtil.findMeetingById(meetingId);
        int currentUser = meeting.getCurrentParticipants();

        //MeetingParticipant에서 삭제
        MeetingParticipant meetingParticipant = meetingParticipantRepository.findByMeeting_IdAndUser_Id(meetingId, userId).get();
        meetingParticipantRepository.delete(meetingParticipant);
        //현재 인원 빼주기.
        meeting.setCurrentParticipants(currentUser - 1);

        //현재 인원 0일때, 모임삭제
        deleteMeeting(meetingId,userId);

    }



    // 모임 참가자 목록 조회
    @Transactional(readOnly = true)
    public List<ParticipantsResponseDto> viewMeetingParticipants(Long meetingId) {
        if (!meetingParticipantRepository.existsByMeeting_Id(meetingId)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"해당 meeting은 존재하지 않습니다! ::  meetingId = " + meetingId);
        }


        List<MeetingParticipant> meetings = meetingParticipantRepository.findByMeeting_Id(meetingId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.UNAUTHORIZED,"해당하는 id가 없습니다 ::  id =" + meetingId)
        );

        List<ParticipantsResponseDto> users = new ArrayList<>();

        for (MeetingParticipant meeting : meetings) {
            ParticipantsResponseDto user = new ParticipantsResponseDto();
            user.setId(meeting.getUser().getId());
            user.setName(meeting.getUser().getName());

            users.add(user);
        }

        return users;
    }
}
