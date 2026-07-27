package com.project.turtlely.domain.notification.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class FcmService {

    public void sendNotification(String targetToken, String title, String body) {
        if (targetToken == null || targetToken.isBlank()) {
            log.warn("FCM 토큰이 존재하지 않아 푸시 알림을 전송하지 않습니다.");
            return;
        }

        try {
            Message message = Message.builder()
                    .setToken(targetToken)
                    .setNotification(Notification.builder()
                            .setTitle(title)
                            .setBody(body)
                            .build())
                    .build();

            String response = FirebaseMessaging.getInstance().send(message);
            log.info("FCM 푸시 알림 전송 성공: {}", response);

        } catch (Exception e) {
            log.error("FCM 푸시 알림 전송 실패: {}", e.getMessage());
        }
    }
}