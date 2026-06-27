package com.project.turtlely.domain.member.entity;

import com.project.turtlely.domain.member.enums.Role;
import com.project.turtlely.domain.member.enums.SocialType;
import com.project.turtlely.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberId;

    @Column(name = "login_id", length = 50)
    private String loginId;

    private String password;

    @Column(length = 50)
    private String nickname;

    @Enumerated(EnumType.STRING)
    @Column(name = "social_type")
    private SocialType socialType;

    private String phoneNumber;
    private String socialId;

    @Enumerated(EnumType.STRING)
    private Role role; // USER, ADMIN

    // 알림 신청 상태 저장할 필드(기본값 false)
    @Builder.Default
    @Column(name = "is_measurement_alarm", nullable = false)
    private boolean isMeasurementAlarm = false;

    @Builder.Default
    @Column(name = "is_report_alarm", nullable = false)
    private boolean isReportAlarm = false;

    // 비번 변경 시 사용
    public void updatePassword(String encodedPassword) {
        this.password = encodedPassword;
    }

    // 서비스 레이어에서 호출 시 JPA 변경 감지를 통해 DB에 자동 반영됨
    public void updateMeasurementAlarm(boolean status) {
        this.isMeasurementAlarm = status;
    }
    // 서비스 레이어에서 호출 시 JPA 변경 감지를 통해 DB에 자동 반영됨
    public void updateReportAlarm(boolean status) {
        this.isReportAlarm = status;
    }
}
