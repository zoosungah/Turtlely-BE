package com.project.turtlely.domain.daily.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

public class DailyRequestDTO {

    @Getter
    @NoArgsConstructor
    public static class DailyReportDetailOpts {
        @Schema(description = "조회할 리포트 날짜", example = "2026-06-27")
        private LocalDate date;
    }
}