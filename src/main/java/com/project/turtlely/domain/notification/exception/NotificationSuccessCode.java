package com.project.turtlely.domain.notification.exception;

import com.project.turtlely.global.apiPayload.code.BaseSuccessCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum NotificationSuccessCode implements BaseSuccessCode {

    NOTIFICATION_GET_SUCCESS(HttpStatus.OK, "NOTI200_1", "최근 7일간의 알림 목록 조회가 완료되었습니다.");

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