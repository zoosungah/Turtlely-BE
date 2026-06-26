package com.project.turtlely.global.apiPayload.code;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * 공통 성공 코드
 */
@AllArgsConstructor
@Getter
public enum GeneralSuccessCode implements BaseSuccessCode {

    OK(HttpStatus.OK,
            "COMMON200",
            "성공적으로 요청을 처리했습니다."),

    REPORT_DETAIL_200(HttpStatus.OK, "REPORT_DETAIL_200", "특정 월간 리포트 조회가 완료되었습니다."),
    REPORT_NOT_FOUND_200(HttpStatus.OK, "REPORT_DETAIL_200", "해당 월의 정기 측정 기록이 존재하지 않습니다."),
    REPORT_ANALYZE_200(HttpStatus.OK, "REPORT_ANALYZE_200", "성공적으로 요청을 처리했습니다."),
    REPORT_ALARM_SET_200(HttpStatus.OK, "REPORT_ALARM_SET_200", "알림 설정이 완료되었습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}