package com.project.turtlely.domain.member.exception;

import com.project.turtlely.global.apiPayload.code.BaseErrorCode;
import com.project.turtlely.global.exception.GeneralException;

public class MemberException extends GeneralException {
    public MemberException(BaseErrorCode code) {
        super(code);
    }
}
