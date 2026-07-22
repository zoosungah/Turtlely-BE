package com.project.turtlely.domain.notification.repository;

import com.project.turtlely.domain.member.entity.Member;
import com.project.turtlely.domain.notification.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // 최근 7일 알림 최신순 조회
    Page<Notification> findByMemberAndSentAtAfterOrderBySentAtDesc(
            Member member,
            LocalDateTime date,
            Pageable pageable
    );
}