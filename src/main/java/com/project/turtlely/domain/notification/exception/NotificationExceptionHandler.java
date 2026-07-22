package com.project.turtlely.domain.notification.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice(basePackages = "com.project.turtlely.domain.notification")
public class NotificationExceptionHandler {

    @ExceptionHandler(NotificationErrorCode.NotificationCustomException.class)
    public ResponseEntity<Object> handleNotificationCustomException(NotificationErrorCode.NotificationCustomException e) {
        NotificationErrorCode errorCode = e.getErrorCode();

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("isSuccess", false);
        body.put("code", errorCode.getCode());
        body.put("message", errorCode.getMessage());
        body.put("result", errorCode.getMessage());

        return new ResponseEntity<>(body, errorCode.getStatus());
    }
}