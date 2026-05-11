package com.project.turtlely.domain.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;


public record ReissueRequest(
        @Schema(description = "refreshToken", example = "dfdfdfkdfkwdkfefiwenj")
        @NotBlank(message = "refreshToken은 필수입니다.")
        String refreshToken
) {
}