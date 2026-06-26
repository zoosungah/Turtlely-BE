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

    @Schema(description = "종합 자세 점수 (0 ~ 100)", example = "85")
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

    @Schema(description = "GPT 기반 AI 종합 분석 소견 텍스트", example = "현재 CVA 각도가 정상 범위보다 낮아 목이 앞으로 다소 돌출된 상태입니다. 모니터 높이를 올리고 스트레칭을 늘려주세요.")
    @JsonProperty("general_opinion")
    private String generalOpinion;

    @Schema(description = "위험도 기준 유관 유발 예측 질환 Top 3", example = "[\"목디스크\", \"근막통증증후군\", \"경추관협착증\"]")
    @JsonProperty("top3_diseases")
    private List<String> top3Diseases;

    @Schema(description = "미래 거북목 악화 시뮬레이션 예측 그래프 데이터")
    @JsonProperty("prediction_graph")
    private List<PredictionGraphDto> predictionGraph;

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
    @Schema(description = "미래 예측 차트 DTO")
    public static class PredictionGraphDto {
        @Schema(description = "예측 대상 월", example = "7월")
        private String month;

        @Schema(description = "예측 악화 각도 수치", example = "45.1")
        private Double angle;
    }
}