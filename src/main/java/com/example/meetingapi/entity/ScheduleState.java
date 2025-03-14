package com.example.meetingapi.entity;

import lombok.Getter;

@Getter
public enum ScheduleState {

    ATTENDING("attending"),
    MAYBE("maybe"),
    NOT_ATTENDING("not_attending");

    private final String status;

    ScheduleState(String status) {
        this.status = status;
    }

}
