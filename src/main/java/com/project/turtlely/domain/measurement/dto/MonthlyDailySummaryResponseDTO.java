package com.project.turtlely.domain.measurement.dto;

import lombok.*;

import java.util.List;

public class MonthlyDailySummaryResponseDTO {

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SummaryDTO {
        private int year;
        private int month;
        private List<WeeklyStatDTO> weeklyStats;
        private MonthlyComparisonDTO monthlyComparison;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WeeklyStatDTO {
        private int week;
        private String weekLabel;
        private Double averageCva;
        private Integer normalRatio;
        private Integer cautionRatio;
        private Integer warningRatio;
        private Boolean hasData;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MonthlyComparisonDTO {
        private Integer normalRatioDiff;
        private Integer cautionRatioDiff;
        private Integer warningRatioDiff;
        private Double cvaDiff;
    }
}