package com.project.turtlely.domain.measurement.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AlarmRequest {

    @JsonProperty("alarm_type")
    private String alarmType;
}