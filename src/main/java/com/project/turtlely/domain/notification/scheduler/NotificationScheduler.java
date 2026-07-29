package com.project.turtlely.domain.notification.scheduler;

import com.project.turtlely.domain.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationScheduler {

    private final NotificationService notificationService;

    // 매일 오후 4시 스트레칭 알림 생성
    @Scheduled(cron = "0 0 16 * * *", zone = "Asia/Seoul")
    public void sendDailyStretchingAlarm() {
        notificationService.createDailyStretchingAlerts();
    }

    // 매일 오전 10시 거북목 교정 알림 생성
    @Scheduled(cron = "0 0 10 * * *", zone = "Asia/Seoul")
    public void sendTurtleneckCorrectionAlarm() {
        notificationService.createTurtleneckCorrectionAlerts();}
}