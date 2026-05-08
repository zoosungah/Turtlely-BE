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
    MEMBER_ID_AVAILABLE(HttpStatus.OK, "MEMBER200_1", "사용 가능한 아이디입니다."),
    LOGIN_SUCCESS(HttpStatus.OK, "MEMBER200_2", "로그인에 성공하였습니다."),
    TOKEN_REISSUE_SUCCESS(HttpStatus.OK, "MEMBER200_3", "토큰이 성공적으로 재발급되었습니다.");
    private final HttpStatus status;
    private final String code;
    private final String message;
}
