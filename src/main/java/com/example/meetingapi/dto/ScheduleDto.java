package com.example.meetingapi.dto;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleDto {
    private Long id;
    private String title;
    private LocalDate date;
    private LocalTime time;
    private String location;
}
