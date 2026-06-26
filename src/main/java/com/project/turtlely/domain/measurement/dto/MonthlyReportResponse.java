package com.project.turtlely.domain.measurement.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Schema(description = "월간 리포트 기본 조회 응답")
public class MonthlyReportResponse {

    @JsonProperty("data_status")
    private String dataStatus;

    @JsonProperty("monthly_id")
    private Long monthlyId;

    private String nickname;

    @JsonProperty("posture_type")
    private String postureType;

    private Integer score;

    @JsonProperty("cva_angle")
    private Double cvaAngle;

    @JsonProperty("cra_angle")
    private Double craAngle;

    @JsonProperty("cva_history")
    private List<HistoryDto> cvaHistory;

    @JsonProperty("cra_history")
    private List<HistoryDto> craHistory;

    @JsonProperty("measurement_alarm")
    private boolean measurementAlarm;

    @JsonProperty("report_alarm")
    private boolean reportAlarm;

    @JsonProperty("measured_at")
    private LocalDateTime measuredAt;

    @Getter
    @Builder
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class HistoryDto {
        private String month;
        private Double angle;
    }
}