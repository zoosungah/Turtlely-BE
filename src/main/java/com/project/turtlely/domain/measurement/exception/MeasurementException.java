package com.project.turtlely.domain.measurement.exception;

import com.project.turtlely.domain.measurement.exception.code.MeasurementErrorCode;
import lombok.Getter;

@Getter
public class MeasurementException extends RuntimeException {
    private final MeasurementErrorCode errorCode;

    public MeasurementException(MeasurementErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}