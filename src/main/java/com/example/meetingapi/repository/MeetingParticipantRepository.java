package com.example.meetingapi.repository;

import com.example.meetingapi.entity.MeetingParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MeetingParticipantRepository extends JpaRepository<MeetingParticipant, Long> {
    Optional<List<MeetingParticipant>> findByMeeting_Id(Long meetingId);
    Optional<MeetingParticipant> findByMeeting_IdAndUser_Id(Long meetingId, Long userId);
    boolean existsByMeeting_IdAndUser_Id(Long meetingId, Long userId);
    boolean existsByMeeting_Id(Long meetingId);
}
