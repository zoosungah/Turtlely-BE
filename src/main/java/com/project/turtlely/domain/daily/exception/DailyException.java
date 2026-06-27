package com.project.turtlely.domain.daily.exception;

import com.project.turtlely.global.apiPayload.code.BaseErrorCode;
import com.project.turtlely.global.exception.GeneralException;

public class DailyException extends GeneralException {
    public DailyException(BaseErrorCode code) {
        super(code);
    }
}