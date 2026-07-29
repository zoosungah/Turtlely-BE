package com.project.turtlely.domain.notification.repository;

import com.project.turtlely.domain.member.entity.Member;
import com.project.turtlely.domain.notification.entity.Notification;
import com.project.turtlely.domain.notification.enums.NotificationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // 최근 7일 알림 최신순 조회
    Page<Notification> findByMemberAndSentAtAfterOrderBySentAtDesc(
            Member member,
            LocalDateTime date,
            Pageable pageable
    );

    // 읽음 처리용
    Optional<Notification> findByNotificationIdAndMember(Long notificationId, Member member);

    // 해당 회원의 알림 확인용
    boolean existsByMember(Member member);

    // 해당 회원의 모든 알림 삭제
    void deleteAllByMember(Member member);

    // 특정 회원, 특정 알림 타입에 대해 지정한 시각 이후 알림 존재 여부 확인 (당일 중복 알림 방지용)
    boolean existsByMemberAndTypeAndSentAtAfter(Member member, NotificationType type, LocalDateTime sentAt);
}