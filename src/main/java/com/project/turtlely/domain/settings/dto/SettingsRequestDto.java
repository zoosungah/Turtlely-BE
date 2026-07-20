package com.project.turtlely.domain.settings.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class SettingsRequestDto {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "닉네임 변경 요청 DTO")
    public static class UpdateNickname {
        @Schema(description = "새로운 닉네임", example = "거북이탈출")
        @NotBlank(message = "닉네임은 필수 입력 항목입니다.")
        @Size(min = 2, max = 10, message = "닉네임은 2~10자 사이여야 합니다.")
        private String nickname;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "비밀번호 재설정 요청 DTO")
    public static class ResetPassword {
        @Schema(description = "현재 비밀번호", example = "Password123!")
        @NotBlank(message = "현재 비밀번호를 입력해주세요.")
        private String currentPassword;

        @Schema(description = "새로운 비밀번호", example = "NewPassword123!")
        @NotBlank(message = "새로운 비밀번호를 입력해주세요.")
        private String newPassword;
    }
}
