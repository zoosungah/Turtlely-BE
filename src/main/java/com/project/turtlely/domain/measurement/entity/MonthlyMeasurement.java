package com.project.turtlely.domain.measurement.entity;

import com.project.turtlely.domain.member.entity.Member;
import com.project.turtlely.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MonthlyMeasurement extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long monthlyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private float cvaAngle;
    private float craAngle;
    private String postureType;
    private LocalDateTime measuredAt;
    private int score;

    @Column(columnDefinition = "TEXT")
    private String predictedDiseases;
    public List<String> getPredictedDiseasesList() {
        if (this.predictedDiseases == null || this.predictedDiseases.isBlank()) return List.of();
        return Arrays.asList(this.predictedDiseases.split(","));
    }

    @Column(columnDefinition = "TEXT")
    private String predictionData;

    public List<String> getPredictionMonthsList() {
        if (this.predictionData == null || !this.predictionData.contains("|")) return List.of();
        String monthsPart = this.predictionData.split("\\|")[0];
        if (monthsPart.isBlank()) return List.of();
        return Arrays.asList(monthsPart.split(","));
    }

    public List<Integer> getPredictionScoresList() {
        if (this.predictionData == null || !this.predictionData.contains("|")) return List.of();
        String[] parts = this.predictionData.split("\\|");
        if (parts.length < 2 || parts[1].isBlank()) return List.of();
        return Arrays.stream(parts[1].split(","))
                .map(Integer::parseInt)
                .collect(Collectors.toList());
    }

    // 하드웨어 데이터, 상수
    private Float hwAccelX;
    private Float hwAccelY;
    private Float hwAccelZ;
    private Float calibrationC;

    public void updateHardwareCalibration(float hwAccelX, float hwAccelY, float hwAccelZ, float calibrationC) {
        this.hwAccelX = hwAccelX;
        this.hwAccelY = hwAccelY;
        this.hwAccelZ = hwAccelZ;
        this.calibrationC = calibrationC;
    }
}