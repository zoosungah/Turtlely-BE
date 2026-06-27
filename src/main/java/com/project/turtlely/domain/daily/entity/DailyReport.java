package com.project.turtlely.domain.daily.entity;

import com.project.turtlely.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "daily_report")
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DailyReport extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "daily_report_id")
    private Long dailyReportId;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(name = "report_date", nullable = false)
    private LocalDate reportDate;

    @Column(name = "total_score", nullable = false)
    private int totalScore;

    @Column(name = "cva_sum", nullable = false)
    private int cvaSum;

    @Column(name = "total_measurement_duration", nullable = false)
    private int totalMeasurementDuration;

    @Column(name = "normal_duration", nullable = false)
    private int normalDuration;

    @Column(name = "caution_duration", nullable = false)
    private int cautionDuration;

    @Column(name = "warning_duration", nullable = false)
    private int warningDuration;

    @Column(name = "avg_angle", nullable = false)
    private double avgAngle;

    @Column(name = "total_notification_count", nullable = false)
    private int totalNotificationCount;
}