package com.project.turtlely.domain.member.exception.code;

import com.project.turtlely.global.apiPayload.code.BaseSuccessCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * 회원 관련 성공 코드 정의
 */

@Getter
@AllArgsConstructor
public enum MemberSuccessCode implements BaseSuccessCode {
    // 회원가입 관련 성공 코드
    MEMBER_ID_AVAILABLE(HttpStatus.OK, "MEMBER200_1", "사용 가능한 아이디입니다."),
    MEMBER_SIGNUP_SUCCESS(HttpStatus.OK, "MEMBER200_4", "회원가입이 완료되었습니다."),

    // 로그아웃 관련 성공 코드
    MEMBER_LOGOUT_SUCCESS(HttpStatus.OK, "MEMBER200_5", "로그아웃이 완료되었습니다."),

    // 아이디/비번 찾기 관련 성공 코드
    MEMBER_FIND_ID_SUCCESS(HttpStatus.OK, "MEMBER200_6", "아이디 찾기에 성공하였습니다."),
    MEMBER__FIND_PW_SUCCESS(HttpStatus.OK, "AUTH200_7", "임시 비밀번호가 발송되었습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
