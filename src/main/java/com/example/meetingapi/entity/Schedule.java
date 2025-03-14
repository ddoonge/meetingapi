package com.example.meetingapi.entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "schedules")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Schedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;
    private String location;
    private LocalDate date;
    private LocalTime time;

    //속한 미팅 다 대 1
    @ManyToOne
    @JoinColumn(name = "meeting_id")
    private Meeting meeting;

    //생성한 유저 다 대 1
    @ManyToOne
    @JoinColumn(name = "create_user_id")
    private User createUser;


    @OneToMany(mappedBy = "schedule", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ScheduleParticipant> scheduleParticipants;
}
