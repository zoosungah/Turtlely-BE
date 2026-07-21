package com.project.turtlely.domain.exercise.exception;

import com.project.turtlely.global.apiPayload.code.BaseErrorCode;
import com.project.turtlely.global.exception.GeneralException;

public class ExcerciseException extends GeneralException {
    public ExcerciseException(BaseErrorCode code) {
        super(code);
    }
}
