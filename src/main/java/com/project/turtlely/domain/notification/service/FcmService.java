package com.project.turtlely.domain.notification.service;

import com.google.firebase.messaging.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
public class FcmService {

    @Async
    public void sendNotification(String targetToken, String title, String body) {
        if (targetToken == null || targetToken.isBlank()) {
            log.warn("FCM 토큰이 존재하지 않아 푸시 알림을 전송하지 않습니다.");
            return;
        }

        int maxRetries = 2; // 네트워크 튐 및 Handshake 단절 대비 2회 재시도 설정

        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                String notificationTag = UUID.randomUUID().toString();

                AndroidConfig androidConfig = AndroidConfig.builder()
                        .setPriority(AndroidConfig.Priority.HIGH) // 도즈 모드 지연 방지를 위한 전송 우선순위 설정
                        .setNotification(AndroidNotification.builder()
                                .setTag(notificationTag)
                                .setChannelId("high_importance_channel") // 프론트엔드와 맞춘 안드로이드 알림 채널 ID
                                .setPriority(AndroidNotification.Priority.HIGH) // 상단 배너 알림 노출을 위한 우선순위 설정
                                .build())
                        .build();

                // 1. APNs 기본 설정을 위한 Aps 객체 추가
                Aps aps = Aps.builder()
                        .setSound("default")
                        .build();

                // 2. setAps(aps)를 명시적으로 주입
                ApnsConfig apnsConfig = ApnsConfig.builder()
                        .putHeader("apns-collapse-id", notificationTag)
                        .putHeader("apns-priority", "10") // iOS 즉시 전송 우선순위 설정 (10: 즉시 전송)
                        .setAps(aps) // <- 필수 지정
                        .build();

                Message message = Message.builder()
                        .setToken(targetToken)
                        .setNotification(Notification.builder()
                                .setTitle(title)
                                .setBody(body)
                                .build())
                        .setAndroidConfig(androidConfig)
                        .setApnsConfig(apnsConfig)
                        .build();

                String response = FirebaseMessaging.getInstance().send(message);
                log.info("FCM 푸시 알림 전송 성공 (시도 {}/{}): {}", attempt, maxRetries, response);
                return; // 성공 시 메서드 종료

            } catch (Exception e) {
                log.warn("FCM 푸시 알림 전송 실패 (시도 {}/{}): {}", attempt, maxRetries, e.getMessage());

                if (attempt < maxRetries) {
                    try {
                        Thread.sleep(500); // 0.5초 대기 후 재연결 시도
                    } catch (InterruptedException ignored) {
                        Thread.currentThread().interrupt();
                    }
                } else {
                    log.error("FCM 푸시 알림 최종 전송 실패 - Token: {}, Exception: ", targetToken, e);
                }
            }
        }
    }
}