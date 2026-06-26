package com.project.turtlely.domain.measurement.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "월간 리포트 알림 신청 요청 데이터")
public class AlarmRequest {

    @Schema(description = "신청할 알림 유형 (MEASURE: 정기 측정 주기 알림 / RESULT: 리포트 발행 완료 알림)", example = "MEASURE")
    private String alarmType;
}