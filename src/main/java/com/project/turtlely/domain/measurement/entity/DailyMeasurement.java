package com.project.turtlely.domain.measurement.entity;

import com.project.turtlely.domain.measurement.enums.Level;
import com.project.turtlely.domain.measurement.enums.PostureStatus;
import com.project.turtlely.domain.member.entity.Member;
import com.project.turtlely.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
@Getter
public class DailyMeasurement extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long dailyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private LocalDateTime measuredAt;
    private float angle;

    @Enumerated(EnumType.STRING)
    private PostureStatus postureStatus; // NORMAL, CAUTION, WARNING

    private boolean notificationTrigger;
    private int duration;

    @Enumerated(EnumType.STRING)
    private Level level; // EASY, NORMAL, HARD

    private Integer batteryLevel;

}
