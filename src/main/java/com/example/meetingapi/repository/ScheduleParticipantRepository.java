package com.example.meetingapi.repository;

import com.example.meetingapi.entity.ScheduleParticipant;
import com.example.meetingapi.entity.ScheduleState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ScheduleParticipantRepository extends JpaRepository<ScheduleParticipant, Long> {
    Optional<ScheduleParticipant> findBySchedule_IdAndUser_Id(Long scheduleId, Long userId);
    boolean existsBySchedule_IdAndUser_IdAndUserState(Long scheduleId, Long userId, ScheduleState userState);
    boolean existsBySchedule_IdAndUser_Id(Long scheduleId, Long userId);
    List<ScheduleParticipant> findBySchedule_IdAndUserState(Long scheduleId, ScheduleState userState);

}