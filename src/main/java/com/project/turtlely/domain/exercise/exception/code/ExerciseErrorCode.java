package com.project.turtlely.domain.exercise.exception.code;

import com.project.turtlely.global.apiPayload.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * 운동존 관련 에러 코드 정의
 */
@Getter
@AllArgsConstructor
public enum ExerciseErrorCode implements BaseErrorCode {

    VIDEO_NOT_FOUND(HttpStatus.NOT_FOUND, "VIDEO_NOT_FOUND", "요청 ID에 해당하는 운동 영상이 존재하지 않습니다."),
    EX_PARAM_ERROR(HttpStatus.BAD_REQUEST, "EX_PARAM_ERROR", "잘못된 연도 또는 월 파라미터 값입니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}