package com.example.meetingapi.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "schedule_participants")
@Getter
@Setter
@NoArgsConstructor
public class ScheduleParticipant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //스케쥴 정보
    @ManyToOne
    @JoinColumn(name = "schedule_id", nullable = false)
    private Schedule schedule;


    //스케쥴에 참여한 유저 정보
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;


    //참여한 스케쥴에 대한 유저 상태
    private ScheduleState userState;


    public ScheduleParticipant(User user, Schedule schedule, ScheduleState userState) {
        this.schedule = schedule;
        this.user = user;
        this.userState = userState;
    }
}
