package com.project.turtlely.domain.daily.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.List;

public class DailyResponseDTO {

    /**
     * 일일 리포트 조회 시 필요한 DTO
     */
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DailyReportDTO {

        @Schema(description = "오늘의 자세 유지 점수", example = "72")
        private int postureScore;

        @Schema(description = "평균 CVA(두척추각) 각도", example = "22.1")
        private double averageCva;

        @Schema(description = "주의(Caution) 발생 횟수", example = "5")
        private long cautionCount;

        @Schema(description = "경고(Warning) 발생 횟수", example = "3")
        private long warningCount;
    }

    /**
     * 리포트 ID 조회할 때 필요한 DTO
     */
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "캘린더 화면 표시용 개별 일일 리포트 요약 정보")
    public static class CalendarReportDTO {

        @Schema(description = "일일 리포트 고유 ID (상세 조회 API 호출 시 사용, 리포트가 없는 날은 null)", example = "1")
        private Long dailyId;

        @Schema(description = "해당 일자의 거북목 자세 유지 점수 (리포트가 없는 날은 null)", example = "72")
        private Integer totalScore;

        @Schema(description = "리포트 해당 날짜 (YYYY-MM-DD 포맷)", example = "2026-06-27")
        private String reportDate;

        @Schema(description = "해당 날짜에 리포트가 존재하는지 여부 (초록 불 활성화용)", example = "true")
        private Boolean hasReport;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "캘린der 전체 기록 조회 응답 wrapper 목록")
    public static class CalendarListDTO {

        @Schema(description = "유저의 전체 캘린더 리포트 데이터 리스트")
        private List<CalendarReportDTO> calendarReports;
    }
}