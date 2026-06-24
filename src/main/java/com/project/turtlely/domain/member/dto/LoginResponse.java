package com.project.turtlely.domain.member.dto;

public record LoginResponse(
        String accessToken,
        String refreshToken,
        boolean isNewUser,
        String socialId,
        Long memberId
) {
}