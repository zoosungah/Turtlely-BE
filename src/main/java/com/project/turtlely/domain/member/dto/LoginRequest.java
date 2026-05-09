package com.project.turtlely.domain.member.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;


public record LoginRequest(
        @NotBlank(message = "아이디는 필수 입력 값입니다.")
        @Size(min = 6, max = 20, message = "아이디는 6~20자 사이여야 합니다.")
        String loginId,

        @NotBlank(message = "비밀번호는 필수 입력 값입니다.")

        String password
) {
}