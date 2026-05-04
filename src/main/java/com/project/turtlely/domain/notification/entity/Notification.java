package com.project.turtlely.domain.notification.entity;

import com.project.turtlely.domain.notification.enums.NotificationStatus;
import com.project.turtlely.domain.notification.enums.NotificationType;
import com.project.turtlely.domain.signup.entity.Member;
import com.project.turtlely.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
@Getter
public class Notification extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long notificationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Enumerated(EnumType.STRING)
    private NotificationType type; // DAILY, MONTHLY, BATTERY, SYSTEM

    @Column(columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    private NotificationStatus status; // SENT, READ, DELETED

    private LocalDateTime sentAt;
    private LocalDateTime readAt;
    private LocalDateTime deletedAt;
}