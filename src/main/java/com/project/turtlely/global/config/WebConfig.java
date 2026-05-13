package com.project.turtlely.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 모든 API 경로에 대해
                .allowedOrigins("http://localhost:64600", // 프론트 기존 포트
                                "http://localhost:3000", // 프론트 새 포트
                                "http://54.144.66.35.nip.io:8080") // 백엔드 배포 주소
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS") // 허용할 HTTP 메서드
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
}