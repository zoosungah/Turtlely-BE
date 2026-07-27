package com.project.turtlely.domain.exercise.exception.code;

import com.project.turtlely.global.apiPayload.code.BaseSuccessCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * 운동존 관련 성공 코드 정의
 */
@Getter
@AllArgsConstructor
public enum ExerciseSuccessCode implements BaseSuccessCode {

    // 운동존 관련 성공 코드
    EXERCISE_LIST_GET_SUCCESS(HttpStatus.OK, "EXERCISE200_1", "운동존 목록 조회에 성공하였습니다."),
    INIT_VIDEOS_SUCCESS(HttpStatus.OK, "EXERCISE200_2", "초기 운동 영상 수집에 성공하였습니다."),
    BOOKMARK_SUCCESS(HttpStatus.OK, "BOOKMARK_SUCCESS", "북마크 상태가 변경되었습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}