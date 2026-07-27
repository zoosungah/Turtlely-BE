package com.project.turtlely.domain.notification.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Slf4j
@Configuration
public class FirebaseConfig {

    @Value("${firebase.service-account-json}")
    private String firebaseConfigJson;

    @PostConstruct
    public void init() {
        try {
            if (firebaseConfigJson == null || firebaseConfigJson.isBlank()) {
                log.warn("Firebase 설정 값이 존재하지 않아 FirebaseApp을 초기화하지 않습니다.");
                return;
            }

            InputStream serviceAccount = new ByteArrayInputStream(
                    firebaseConfigJson.getBytes(StandardCharsets.UTF_8)
            );

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
                log.info("Firebase Application이 성공적으로 초기화되었습니다.");
            }
        } catch (Exception e) {
            log.error("Firebase 초기화 중 에러 발생: {}", e.getMessage());
        }
    }
}