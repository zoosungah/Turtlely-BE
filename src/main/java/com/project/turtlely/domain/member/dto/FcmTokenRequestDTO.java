package com.project.turtlely.domain.member.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FcmTokenRequestDTO {
    @NotBlank(message = "FCM 토큰은 필수 입력 항목입니다.")
    private String fcmToken;
}