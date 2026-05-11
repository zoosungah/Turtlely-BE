package com.project.turtlely.domain.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class MemberRequestDTO {
    @Getter
    @NoArgsConstructor
    @Schema(description = "일반 회원가입 요청 객체")
    public static class SignupDTO {

        @Schema(description = "서비스 내 닉네임", example = "거북이")
        private String nickname;

        @Schema(description = "중복 확인이 완료된 아이디", example = "turtle")
        private String loginId;

        @Schema(description = "사용자 비밀번호", example = "turtle03!")
        private String password;

        @Schema(description = "SMS 인증이 완료된 전화번호", example = "01012345678")
        private String phoneNumber;
    }

    @Getter
    @NoArgsConstructor
    @Schema(description = "구글 회원가입 요청 객체")
    public static class SocialSignupDTO {
        @Schema(description = "서비스 내 닉네임", example = "구글거북이")
        private String nickname;

        @Schema(description = "구글 고유 ID(idToken 검증 후 받은 sub)", example = "123456789")
        private String socialId;

        @Schema(description = "SMS 인증이 완료된 전화번호", example = "01012345678")
        private String phoneNumber;
    }
}
