package com.project.turtlely.domain.daily.exception.code;

import com.project.turtlely.global.apiPayload.code.BaseSuccessCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * 일일 리포트 관련 성공 코드 정의
 */
@Getter
@AllArgsConstructor
public enum DailySuccessCode implements BaseSuccessCode {

    // 일일 리포트 관련 성공 코드
    REPORT_GET_SUCCESS(HttpStatus.OK, "REPORT200_1", "일일 리포트 조회에 성공하였습니다."),
    CALENDAR_GET_SUCCESS(HttpStatus.OK, "REPORT200_2", "캘린더 기록 조회에 성공하였습니다.");
    private final HttpStatus status;
    private final String code;
    private final String message;
}