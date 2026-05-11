package com.project.turtlely.domain.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record GoogleLoginRequest(
        @Schema(description = "idToken", example = "dfdfdfkdfkwdkfefiwenj")
        @NotBlank(message = "idToken은 필수입니다.")
        String idToken
) {
}