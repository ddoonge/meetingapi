package com.example.meetingapi.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MeetingDto {
    private Long id;
    private String name;
    private String description;
    private Integer maxParticipants;
    private Integer currentParticipants;
}
