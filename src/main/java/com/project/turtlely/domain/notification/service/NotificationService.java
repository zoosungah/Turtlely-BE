package com.project.turtlely.domain.notification.service;

import com.project.turtlely.domain.member.entity.Member;
import com.project.turtlely.domain.member.repository.MemberRepository;
import com.project.turtlely.domain.notification.entity.Notification;
import com.project.turtlely.domain.notification.enums.NotificationStatus;
import com.project.turtlely.domain.notification.enums.NotificationType;
import com.project.turtlely.domain.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationService {

    private final MemberRepository memberRepository;
    private final NotificationRepository notificationRepository;

    // 매일 오후 4시 전체 회원 대상 데일리 스트레칭 권장 알림 생성
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
}