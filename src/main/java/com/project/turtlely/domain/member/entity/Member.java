package com.project.turtlely.domain.member.entity;

import com.project.turtlely.domain.member.enums.Role;
import com.project.turtlely.domain.member.enums.SocialType;
import com.project.turtlely.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

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

    // 알림 신청 상태 저장할 필드(기본값 false)
    @Builder.Default
    @Column(name = "is_report_alarm", nullable = false)
    private boolean isReportAlarm = false;

    // 측정 알림 신청 일시를 기록하는 필드
    @Column(name = "measurement_alarm_set_at")
    private LocalDateTime measurementAlarmSetAt;

    // 리포트 알림 신청 일시를 기록하는 필드
    @Column(name = "report_alarm_set_at")
    private LocalDateTime reportAlarmSetAt;

    // 서비스 레이어 호출 시 상태 업데이트와 함께 신청 시점 기록/초기화 수행
    public void updateMeasurementAlarm(boolean status) {
        this.isMeasurementAlarm = status;
        this.measurementAlarmSetAt = status ? LocalDateTime.now() : null;
    }

    // 서비스 레이어 호출 시 상태 업데이트와 함께 신청 시점 기록/초기화 수행
    public void updateReportAlarm(boolean status) {
        this.isReportAlarm = status;
        this.reportAlarmSetAt = status ? LocalDateTime.now() : null;
    }

    // 비번 변경 시 사용
    public void updatePassword(String encodedPassword) {
        this.password = encodedPassword;
    }

    // 닉네임 변경 시 사용
    public void updateNickname(String newNickname) {
        this.nickname = newNickname;
    }

    @Column(name = "fcm_token")
    private String fcmToken;

    public void updateFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;}
}
