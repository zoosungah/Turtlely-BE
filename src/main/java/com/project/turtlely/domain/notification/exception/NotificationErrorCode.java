package com.project.turtlely.domain.notification.exception;

import com.project.turtlely.global.apiPayload.code.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum NotificationErrorCode implements BaseErrorCode {

    AUTH_TOKEN_INVALID(HttpStatus.UNAUTHORIZED, "NOTI401_1", "유효하지 않거나 만료된 인증 토큰입니다."),
    ALARM_EMPTY(HttpStatus.NOT_FOUND, "NOTI404_1", "최근 7일 이내에 수신된 알림이 없습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;

    @Override
    public HttpStatus getStatus() {
        return status;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Getter
    public static class NotificationCustomException extends RuntimeException {
        private final NotificationErrorCode errorCode;

        public NotificationCustomException(NotificationErrorCode errorCode) {
            super(errorCode.getMessage());
            this.errorCode = errorCode;
        }
    }
}