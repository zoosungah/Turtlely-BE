package com.project.turtlely.domain.measurement.exception;

import com.project.turtlely.global.apiPayload.code.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum MeasurementErrorCode implements BaseErrorCode {

    INVALID_REPORT_ID(HttpStatus.BAD_REQUEST, "REPORT400_1", "monthly_id가 양수가 아닙니다."),
    REPORT_OPINION_EMPTY(HttpStatus.BAD_REQUEST, "REPORT400_2", "종합 소견이 비어있습니다."),
    REPORT_DISEASES_EMPTY(HttpStatus.BAD_REQUEST, "REPORT400_3", "top3 유발 예측 질환이 비어있습니다."),
    INVALID_FRAME_DATA(HttpStatus.BAD_REQUEST, "REPORT400_4", "업로드된 프레임 배열이 비어있거나 누락됨"),
    INVALID_ALARM_TYPE(HttpStatus.BAD_REQUEST, "REPORT400_5", "alarm_type값이 유효하지 않습니다."),
    ALREADY_ALARM_SET(HttpStatus.BAD_REQUEST, "REPORT400_6", "이미 동일한 유형의 알림 신청이 완료된 상태입니다."),
    REPORT_PROCESSING(HttpStatus.ACCEPTED, "REPORT202_1", "아직 AI 연산 처리가 완료되지 않음 (프론트 로딩 바 유지용)"),
    REPORT_NOT_FOUND(HttpStatus.NOT_FOUND, "REPORT404_1", "요청 monthly_id에 해당하는 리포트를 찾을 수 없음"),
    LANDMARK_NOT_FOUND(HttpStatus.INTERNAL_SERVER_ERROR, "REPORT500_1", "전송된 프레임 중 유효한 랜드마크 범위(0.0~1.0)를 가진 최적 프레임을 판별할 수 없음"),
    LLM_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "REPORT500_2", "외부 AI 연산 모듈(GPT API) 통신 중 에러 발생"),
    SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "REPORT500_3", "서버 내부 오류로 인해 알림 등록 실패");

    private final HttpStatus status;
    private final String code;
    private final String message;

    @Override
    public HttpStatus getStatus() {
        return status;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Getter
    public static class MeasurementCustomException extends RuntimeException {
        private final MeasurementErrorCode errorCode;

        public MeasurementCustomException(MeasurementErrorCode errorCode) {
            super(errorCode.getMessage());
            this.errorCode = errorCode;
        }
    }
}