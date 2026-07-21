package com.project.turtlely.domain.notification.entity;

import com.project.turtlely.domain.member.entity.Member;
import com.project.turtlely.domain.notification.enums.NotificationStatus;
import com.project.turtlely.domain.notification.enums.NotificationType;
import com.project.turtlely.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@AllArgsConstructor
public class Notification extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long notificationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", length = 30)
    private NotificationType type; // DAILY, MONTHLY, BATTERY, SYSTEM, TURTLENECK

    @Column(columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    private NotificationStatus status; // SENT, READ, DELETED

    private LocalDateTime sentAt;
    private LocalDateTime readAt;
    private LocalDateTime deletedAt;
}