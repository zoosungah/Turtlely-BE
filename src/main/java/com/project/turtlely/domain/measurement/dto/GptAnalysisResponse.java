package com.project.turtlely.domain.measurement.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class GptAnalysisResponse {

    private int cervicalHealthScore;
    private String generalOpinion;
    private List<DiseaseDto> top3Diseases;
    private List<PredictionGraphDto> predictionGraph;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class DiseaseDto {
        @io.swagger.v3.oas.annotations.media.Schema(description = "질병 및 증상명", example = "목디스크")
        private String name;

        @io.swagger.v3.oas.annotations.media.Schema(description = "위험도 확률 게이지 퍼센트 (0~100)", example = "73")
        private int probability;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class PredictionGraphDto {
        private String month;
        private Double angle;
    }
}