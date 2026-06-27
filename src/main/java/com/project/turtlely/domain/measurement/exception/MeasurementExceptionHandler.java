package com.project.turtlely.domain.measurement.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice(basePackages = "com.project.turtlely.domain.measurement")
public class MeasurementExceptionHandler {

    @ExceptionHandler(MeasurementErrorCode.MeasurementCustomException.class)
    public ResponseEntity<Object> handleMeasurementCustomException(MeasurementErrorCode.MeasurementCustomException e) {
        MeasurementErrorCode errorCode = e.getErrorCode();

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("isSuccess", false);
        body.put("code", errorCode.getCode());
        body.put("message", errorCode.getMessage());
        body.put("result", errorCode.getMessage());

        return new ResponseEntity<>(body, errorCode.getStatus());
    }
}