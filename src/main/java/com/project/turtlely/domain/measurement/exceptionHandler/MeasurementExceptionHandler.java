package com.project.turtlely.domain.measurement.exceptionHandler;

import com.project.turtlely.domain.measurement.exception.MeasurementException;
import com.project.turtlely.domain.measurement.exception.code.MeasurementErrorCode;
import com.project.turtlely.domain.measurement.exception.code.MeasurementErrorCode.MeasurementCustomException;
import com.project.turtlely.global.apiPayload.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = "com.project.turtlely.domain.measurement")
public class MeasurementExceptionHandler {

    @ExceptionHandler(MeasurementException.class)
    public ResponseEntity<ApiResponse<Object>> handleMeasurementException(MeasurementException e) {
        MeasurementErrorCode errorCode = e.getErrorCode();
        ApiResponse<Object> body = ApiResponse.onFailure(errorCode, null);
        return new ResponseEntity<>(body, errorCode.getStatus());
    }

    @ExceptionHandler(MeasurementCustomException.class)
    public ResponseEntity<ApiResponse<Object>> handleMeasurementCustomException(MeasurementCustomException e) {
        MeasurementErrorCode errorCode = e.getErrorCode();
        ApiResponse<Object> body = ApiResponse.onFailure(errorCode, null);
        return new ResponseEntity<>(body, errorCode.getStatus());
    }
}