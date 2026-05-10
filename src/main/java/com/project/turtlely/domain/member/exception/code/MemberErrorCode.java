package com.project.turtlely.domain.member.exception.code;

import com.project.turtlely.global.apiPayload.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * 회원 관련 에러 코드 정의
 */
@Getter
@AllArgsConstructor
public enum MemberErrorCode implements BaseErrorCode {
    // 회원가입 관련 에러 코드
    MEMBER_ID_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "MEMBER400_1", "이미 존재하는 아이디입니다."),
    MEMBER_ALREADY_SOCIAL_REGISTERED(HttpStatus.BAD_REQUEST, "MEMBER400_4", "이미 가입된 소셜 계정입니다."),

    // 아이디/비번 찾기 관련 에러 코드
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBER404_1", "일치하는 회원 정보가 없습니다.");
    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "MEMBER401_1", "비밀번호가 일치하지 않습니다."),
    LOGIN_INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "MEMBER500_1", "서버 내부 오류가 발생했습니다."),
    INVALID_GOOGLE_TOKEN(HttpStatus.BAD_REQUEST, "MEMBER400_2", "유효하지 않은 구글 토큰입니다."),
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "MEMBER401_2", "토큰이 만료되었을 때"),
    LOGIN_INVALID_PARAMETER(HttpStatus.BAD_REQUEST, "MEMBER400_1", "아이디/비밀번호 형식 오류");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
