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
@Schema(description = "월간 리포트 종합 조회")
public class MonthlyReportResponse {

    @Schema(description = "데이터 존재 여부 상태 (AVAILABLE: 데이터 있음, NOT_YET: 정기 측정 기록 없음)", example = "AVAILABLE")
    @JsonProperty("data_status")
    private String dataStatus;

    @Schema(description = "월간 리포트 고유 식별자 ID", example = "1")
    @JsonProperty("monthly_id")
    private Long monthlyId;

    @Schema(description = "사용자 닉네임", example = "turtle")
    private String nickname;

    @Schema(description = "거북목 상태 유형 분류", example = "거북목")
    @JsonProperty("posture_type")
    private String postureType;

    @Schema(description = "GPT 분석 기반 정밀 경추 건강 점수 (0 ~ 100)", example = "73")
    private Integer score;

    @Schema(description = "최근 CVA 측정 각도 수치", example = "48.5")
    @JsonProperty("cva_angle")
    private Double cvaAngle;

    @Schema(description = "최근 CRA 측정 각도 수치", example = "12.3")
    @JsonProperty("cra_angle")
    private Double craAngle;

    @Schema(description = "월별 CVA 변화 히스토리 리스트 (최근 6개월)")
    @JsonProperty("cva_history")
    private List<HistoryDto> cvaHistory;

    @Schema(description = "월별 CRA 변화 히스토리 리스트 (최근 6개월)")
    @JsonProperty("cra_history")
    private List<HistoryDto> craHistory;

    @Schema(description = "정기 측정 알림 신청 활성화 여부", example = "true")
    @JsonProperty("measurement_alarm")
    private boolean measurementAlarm;

    @Schema(description = "리포트 발행 알림 신청 활성화 여부", example = "false")
    @JsonProperty("report_alarm")
    private boolean reportAlarm;

    @Schema(description = "최종 측정 일시", example = "2026-06-25T14:30:00")
    @JsonProperty("measured_at")
    private LocalDateTime measuredAt;

    @Schema(description = "위험도 기준 유관 유발 예측 질환 Top 3", example = "[\"목디스크\", \"후두신경통\", \"척추측만증\"]")
    @JsonProperty("predicted_diseases")
    private List<String> predictedDiseases;

    @Schema(description = "미래 거북목 악화 시뮬레이션 예측 그래프 데이터")
    @JsonProperty("prediction_data")
    private PredictionDataDto predictionData;

    @Schema(description = "현재 조회 대상 리포트의 연도 정보", example = "2026")
    private Integer reportYear;

    @Schema(description = "현재 조회 대상 리포트의 월 정보", example = "6")
    private Integer reportMonth;

    @Getter
    @Builder
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    @Schema(description = "월별 각도 통계 DTO")
    public static class HistoryDto {
        @Schema(description = "해당 월", example = "1월")
        private String month;

        @Schema(description = "측정 각도", example = "51.2")
        private Double angle;
    }

    @Getter
    @Builder
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    @Schema(description = "미래 예측 차트 데이터 묶음 DTO")
    public static class PredictionDataDto {
        @Schema(description = "예측 대상 월 리스트", example = "[\"6월\", \"7월\", \"8월\", \"9월\", \"10월\", \"11월\"]")
        @JsonProperty("prediction_months")
        private List<String> predictionMonths;

        @Schema(description = "예측 악화 점수 리스트", example = "[73, 75, 78, 80, 82, 85]")
        @JsonProperty("prediction_scores")
        private List<Integer> predictionScores;
    }
}