package com.example.meetingapi.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "meeting_participants")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MeetingParticipant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //유저 정보
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    //미팅 정보
    @ManyToOne
    @JoinColumn(name = "meeting_id", nullable = false)
    private Meeting meeting;


    public MeetingParticipant(User user, Meeting meeting) {
        this.user = user;
        this.meeting = meeting;
    }
}
