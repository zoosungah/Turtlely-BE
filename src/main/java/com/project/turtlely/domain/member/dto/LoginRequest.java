package com.project.turtlely.domain.member.dto;

public record LoginRequest(
        String loginId,
        String password
) {
}