package com.project.turtlely.domain.measurement.entity;

import com.project.turtlely.domain.member.entity.Member;
import com.project.turtlely.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
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

    @Column(columnDefinition = "TEXT")
    private String predictionData;

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
