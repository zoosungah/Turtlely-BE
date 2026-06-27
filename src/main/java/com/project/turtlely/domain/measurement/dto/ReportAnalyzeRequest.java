package com.project.turtlely.domain.measurement.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class ReportAnalyzeRequest {

    private List<FrameData> frames;

    @Getter
    @NoArgsConstructor
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
    }
}