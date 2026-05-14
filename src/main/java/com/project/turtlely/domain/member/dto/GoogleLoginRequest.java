package com.project.turtlely.domain.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record GoogleLoginRequest(
        @Schema(description = "구글 accessToken", example = "ya0AQvPyIP2RrkEwR2G...")
        @NotBlank(message = "accessToken은 필수입니다.")
        String accessToken
) {
}