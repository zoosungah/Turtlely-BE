package com.project.turtlely.domain.measurement.exception;

import com.project.turtlely.global.apiPayload.code.BaseSuccessCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum MeasurementSuccessCode implements BaseSuccessCode {

    REPORT_NOT_FOUND(HttpStatus.OK, "REPORT200_1", "해당 월의 정기 측정 기록이 존재하지 않습니다."),
    REPORT_DETAIL(HttpStatus.OK, "REPORT200_2", "특정 월간 리포트 조회가 완료되었습니다."),
    REPORT_ANALYZE(HttpStatus.OK, "REPORT200_3", "특정 월간 리포트 분석 및 저장이 완료되었습니다."),
    REPORT_ALARM_SET(HttpStatus.OK, "REPORT200_4", "정기 알림 설정이 완료되었습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public HttpStatus getStatus() {
        return httpStatus;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}