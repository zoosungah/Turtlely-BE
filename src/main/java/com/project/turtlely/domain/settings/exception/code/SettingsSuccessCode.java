package com.project.turtlely.domain.settings.exception.code;

import com.project.turtlely.global.apiPayload.code.BaseSuccessCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * 회원 설정 관련 성공 코드 정의
 */
@Getter
@AllArgsConstructor
public enum SettingsSuccessCode implements BaseSuccessCode {

    // 회원 설정 관련 성공 코드
    NICKNAME_GET_SUCCESS(HttpStatus.OK, "SETTING200_1", "닉네임 조회에 성공하였습니다."),
    LOGIN_ID_GET_SUCCESS(HttpStatus.OK, "SETTING200_2", "아이디 조회에 성공하였습니다."),
    NICKNAME_UPDATE_SUCCESS(HttpStatus.OK, "SETTING200_3", "닉네임 변경에 성공하였습니다."),
    PASSWORD_RESET_SUCCESS(HttpStatus.OK, "SETTING200_4", "비밀번호 재설정에 성공하였습니다."),
    LOGOUT_SUCCESS(HttpStatus.OK, "MEMBER200_5", "로그아웃 성공"),
    WITHDRAWAL_SUCCESS(HttpStatus.OK, "MEMBER200_6", "회원 탈퇴가 성공적으로 완료되었습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
