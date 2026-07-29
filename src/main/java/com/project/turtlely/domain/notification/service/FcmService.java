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

        try {
            // 알림별 고유 태그 생성
            String notificationTag = UUID.randomUUID().toString();

            AndroidConfig androidConfig = AndroidConfig.builder()
                    .setNotification(AndroidNotification.builder()
                            .setTag(notificationTag)
                            .build())
                    .build();

            ApnsConfig apnsConfig = ApnsConfig.builder()
                    .putHeader("apns-collapse-id", notificationTag)
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
            log.info("FCM 푸시 알림 전송 성공: {}", response);

        } catch (Exception e) {
            log.error("FCM 푸시 알림 전송 실패: {}", e.getMessage());
        }
    }
}