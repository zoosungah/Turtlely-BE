package com.project.turtlely.domain.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

/**
 * SMS 인증 관련 DTO
 */
public class SmsRequestDTO {
    @Getter
    public static class SmsSendDTO {
        @Schema(description = "수신자 전화번호", example = "01012345678")
        private String phoneNumber;
    }

    @Getter
    public static class SmsVerifyDTO {
        @Schema(description = "수신자 전화번호", example = "01012345678")
        private String phoneNumber;
        @Schema(description = "인증번호 4자리", example = "1234")
        private String verifyCode;
    }
}
