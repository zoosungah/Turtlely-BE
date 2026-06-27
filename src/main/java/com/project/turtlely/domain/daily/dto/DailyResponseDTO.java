package com.project.turtlely.domain.daily.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class DailyResponseDTO {

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
}