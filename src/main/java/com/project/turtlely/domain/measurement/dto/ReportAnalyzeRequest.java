package com.project.turtlely.domain.measurement.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReportAnalyzeRequest {

    @JsonProperty("frames")
    private List<FrameData> frames;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FrameData {

        @JsonProperty("eye_x")
        private double eyeX;

        @JsonProperty("eye_y")
        private double eyeY;

        @JsonProperty("tragus_x")
        private double tragusX;

        @JsonProperty("tragus_y")
        private double tragusY;

        @JsonProperty("c7_x")
        private double c7X;

        @JsonProperty("c7_y")
        private double c7Y;

        // HW 3축 가속도 데이터
        @JsonProperty("hw_accel_x")
        private double hwAccelX;

        @JsonProperty("hw_accel_y")
        private double hwAccelY;

        @JsonProperty("hw_accel_z")
        private double hwAccelZ;
    }
}