package com.project.turtlely.domain.daily.exception.code;

import com.project.turtlely.global.apiPayload.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * 일일 리포트 관련 에러 코드 정의
 */
@Getter
@AllArgsConstructor
public enum DailyErrorCode implements BaseErrorCode {

    REPORT_NOT_FOUND(HttpStatus.NOT_FOUND, "REPORT404_1", "해당 리포트 데이터가 존재하지 않습니다."),
    REPORT_ACCESS_DENIED(HttpStatus.FORBIDDEN, "REPORT403_1", "해당 리포트를 조회할 권한이 없습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}