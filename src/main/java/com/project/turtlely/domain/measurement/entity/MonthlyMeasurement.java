package com.project.turtlely.domain.measurement.entity;

import com.project.turtlely.domain.member.entity.Member;
import com.project.turtlely.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
@Getter
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
}
