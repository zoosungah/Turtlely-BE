package com.project.turtlely.domain.notification.service;

import com.project.turtlely.domain.member.entity.Member;
import com.project.turtlely.domain.member.repository.MemberRepository;
import com.project.turtlely.domain.notification.dto.NotificationResponse.NotificationDto;
import com.project.turtlely.domain.notification.dto.NotificationResponse.NotificationListDto;
import com.project.turtlely.domain.notification.entity.Notification;
import com.project.turtlely.domain.notification.enums.NotificationStatus;
import com.project.turtlely.domain.notification.enums.NotificationType;
import com.project.turtlely.domain.notification.exception.NotificationErrorCode;
import com.project.turtlely.domain.notification.exception.NotificationErrorCode.NotificationCustomException;
import com.project.turtlely.domain.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationService {

    private final MemberRepository memberRepository;
    private final NotificationRepository notificationRepository;

    public NotificationListDto getRecentNotifications(String loginId, Pageable pageable) {
        Member member = memberRepository.findByLoginId(loginId)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자를 찾을 수 없습니다."));

        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);

        Page<Notification> notifications = notificationRepository
                .findByMemberAndSentAtAfterOrderBySentAtDesc(member, sevenDaysAgo, pageable);

        if (notifications.isEmpty()) {
            throw new NotificationCustomException(NotificationErrorCode.ALARM_EMPTY);
        }

        List<NotificationDto> dtoList = notifications.stream()
                .map(n -> NotificationDto.builder()
                        .notificationId(n.getNotificationId())
                        .type(n.getType())
                        .content(n.getContent())
                        .isRead(n.isRead())
                        .createdAt(n.getSentAt())
                        .build())
                .collect(Collectors.toList());

        return NotificationListDto.builder()
                .notificationList(dtoList)
                .build();
    }

    // 알림 읽음 처리 메서드
    @Transactional
    public void readNotification(Long notificationId, String loginId) {
        Member member = memberRepository.findByLoginId(loginId)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자를 찾을 수 없습니다."));

        Notification notification = notificationRepository.findByNotificationIdAndMember(notificationId, member)
                .orElseThrow(() -> new NotificationCustomException(NotificationErrorCode.NOTIFICATION_NOT_FOUND));

        notification.markAsRead();
    }

    @Transactional
    public void createDailyStretchingAlerts() {
        List<Member> allMembers = memberRepository.findAll();

        for (Member member : allMembers) {
            Notification notification = Notification.builder()
                    .member(member)
                    .type(NotificationType.DAILY)
                    .content("가볍게 스트레칭하면서 긴장된 목을 풀어보세요")
                    .status(NotificationStatus.SENT)
                    .sentAt(LocalDateTime.now())
                    .build();

            notificationRepository.save(notification);
        }
        notificationRepository.flush();
    }

    @Transactional
    public void createTurtleneckCorrectionAlerts() {
        List<Member> allMembers = memberRepository.findAll();

        for (Member member : allMembers) {
            String content = member.getNickname() + "님 작업 중이신가요? 거북목을 교정할 시간이에요";

            Notification notification = Notification.builder()
                    .member(member)
                    .type(NotificationType.TURTLENECK)
                    .content(content)
                    .status(NotificationStatus.SENT)
                    .sentAt(LocalDateTime.now())
                    .build();

            notificationRepository.save(notification);
        }
        notificationRepository.flush();
    }
}