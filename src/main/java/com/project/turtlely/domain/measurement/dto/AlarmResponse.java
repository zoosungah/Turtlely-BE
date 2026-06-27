package com.project.turtlely.domain.measurement.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AlarmResponse {

    @JsonProperty("alarm_type")
    private String alarmType;

    @JsonProperty("is_alarm_set")
    private boolean alarmSet;
}