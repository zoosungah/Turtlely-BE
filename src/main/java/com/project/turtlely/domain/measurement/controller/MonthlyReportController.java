package com.project.turtlely.domain.measurement.controller;

import com.project.turtlely.domain.measurement.dto.AlarmRequest;
import com.project.turtlely.domain.measurement.dto.AlarmResponse;
import com.project.turtlely.domain.measurement.dto.MonthlyReportResponse;
import com.project.turtlely.domain.measurement.dto.ReportAnalyzeRequest;
import com.project.turtlely.domain.measurement.service.MonthlyReportService;
import com.project.turtlely.domain.member.entity.Member;
import com.project.turtlely.domain.measurement.exception.MeasurementSuccessCode;
import com.project.turtlely.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "월간측정/월간리포트")
@RestController
@RequestMapping("/api/monthly")
@RequiredArgsConstructor
@SecurityRequirement(name = "JWT_TOKEN")
public class MonthlyReportController {

    private final MonthlyReportService monthlyReportService;

    @Operation(summary = "월간 리포트 종합 조회 API by 김승연(개발완료)", description = "월간 리포트 상세 분석 데이터 내용을 종합 조회합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "조회 성공 명세",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "1. 해당 월에 측정 데이터가 존재할 때 (AVAILABLE)",
                                            value = "{\n" +
                                                    "  \"isSuccess\": true,\n" +
                                                    "  \"code\": \"REPORT200_2\",\n" +
                                                    "  \"message\": \"특정 월간 리포트 조회가 완료되었습니다.\",\n" +
                                                    "  \"result\": {\n" +
                                                    "    \"data_status\": \"AVAILABLE\",\n" +
                                                    "    \"monthly_id\": 16,\n" +
                                                    "    \"nickname\": \"seungyeon\",\n" +
                                                    "    \"posture_type\": \"역C자목\",\n" +
                                                    "    \"score\": 73,\n" +
                                                    "    \"cva_angle\": 48.5,\n" +
                                                    "    \"cra_angle\": 12.3,\n" +
                                                    "    \"cva_history\": [{\"month\": \"6월\", \"angle\": 48.5}],\n" +
                                                    "    \"cra_history\": [{\"month\": \"6월\", \"angle\": 12.3}],\n" +
                                                    "    \"measurement_alarm\": true,\n" +
                                                    "    \"report_alarm\": true,\n" +
                                                    "    \"measured_at\": \"2026-06-26T20:53:11\",\n" +
                                                    "    \"predicted_diseases\": [\"목디스크\", \"후두신경통\", \"척추측만증\"],\n" +
                                                    "    \"prediction_data\": {\n" +
                                                    "      \"prediction_months\": [\"6월\", \"7월\", \"8월\", \"9월\", \"10월\", \"11월\"],\n" +
                                                    "      \"prediction_scores\": [73, 75, 78, 80, 82, 85]\n" +
                                                    "    }\n" +
                                                    "  }\n" +
                                                    "}"
                                    ),
                                    @ExampleObject(
                                            name = "2. 해당 월의 정기 측정 기록이 존재하지 않을 때 (NOT_YET)",
                                            value = "{\n" +
                                                    "  \"isSuccess\": true,\n" +
                                                    "  \"code\": \"REPORT200_1\",\n" +
                                                    "  \"message\": \"해당 월의 정기 측정 기록이 존재하지 않습니다.\",\n" +
                                                    "  \"result\": {\n" +
                                                    "    \"data_status\": \"NOT_YET\",\n" +
                                                    "    \"monthly_id\": null,\n" +
                                                    "    \"nickname\": \"turtle\",\n" +
                                                    "    \"posture_type\": \"데이터 없음\",\n" +
                                                    "    \"score\": null,\n" +
                                                    "    \"cva_angle\": null,\n" +
                                                    "    \"cra_angle\": null,\n" +
                                                    "    \"cva_history\": [],\n" +
                                                    "    \"cra_history\": [],\n" +
                                                    "    \"measurement_alarm\": false,\n" +
                                                    "    \"report_alarm\": false,\n" +
                                                    "    \"measured_at\": null,\n" +
                                                    "    \"predicted_diseases\": [],\n" +
                                                    "    \"prediction_data\": {\n" +
                                                    "      \"prediction_months\": [],\n" +
                                                    "      \"prediction_scores\": []\n" +
                                                    "    }\n" +
                                                    "  }\n" +
                                                    "}"
                                    )
                            }
                    )
            )
    })
    @GetMapping("/{monthly_id}")
    public ResponseEntity<ApiResponse<MonthlyReportResponse>> getMonthlyReport(
            @Parameter(hidden = true) @RequestAttribute("member") Member member,
            @Parameter(description = "", example = "1") @PathVariable("monthly_id") Long monthlyId) {

        MonthlyReportResponse response = monthlyReportService.getMonthlyReport(monthlyId, member);

        if ("NOT_YET".equals(response.getDataStatus())) {
            return ResponseEntity.ok(ApiResponse.onSuccess(MeasurementSuccessCode.REPORT_NOT_FOUND, response));
        }

        return ResponseEntity.ok(ApiResponse.onSuccess(MeasurementSuccessCode.REPORT_DETAIL, response));
    }

    @Operation(summary = "월간 측정용 프레임 좌표 분석 API by 김승연(개발완료)", description = "수집된 프레임 좌표 배열을 분석하여 거북목 상태 결과를 생성 및 저장합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "분석 및 연산 처리 저장 완료",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "분석 완료 성공 반환값 명세",
                                    // 💡 [싱크 일치화] 종합 조회 성공(AVAILABLE) 반환값 구조와 텍스트 키값 순서를 토씨 하나 안 틀리고 100% 동일화했습니다.
                                    value = "{\n" +
                                            "  \"isSuccess\": true,\n" +
                                            "  \"code\": \"REPORT200_3\",\n" +
                                            "  \"message\": \"특정 월간 리포트 분석 및 저장이 완료되었습니다.\",\n" +
                                            "  \"result\": {\n" +
                                            "    \"data_status\": \"AVAILABLE\",\n" +
                                            "    \"monthly_id\": 16,\n" +
                                            "    \"nickname\": \"seungyeon\",\n" +
                                            "    \"posture_type\": \"역C자목\",\n" +
                                            "    \"score\": 73,\n" +
                                            "    \"cva_angle\": 48.5,\n" +
                                            "    \"cra_angle\": 12.3,\n" +
                                            "    \"cva_history\": [{\"month\": \"6월\", \"angle\": 48.5}],\n" +
                                            "    \"cra_history\": [{\"month\": \"6월\", \"angle\": 12.3}],\n" +
                                            "    \"measurement_alarm\": true,\n" +
                                            "    \"report_alarm\": true,\n" +
                                            "    \"measured_at\": \"2026-06-26T20:53:11\",\n" +
                                            "    \"predicted_diseases\": [\"목디스크\", \"후두신경통\", \"척추측만증\"],\n" +
                                            "    \"report_alarm\": true,\n" +
                                            "    \"prediction_data\": {\n" +
                                            "      \"prediction_months\": [\"6월\", \"7월\", \"8월\", \"9월\", \"10월\", \"11월\"],\n" +
                                            "      \"prediction_scores\": [73, 75, 78, 80, 82, 85]\n" +
                                            "    }\n" +
                                            "  }\n" +
                                            "}"
                            )
                    )
            )
    })
    @PostMapping("/measurements")
    public ResponseEntity<ApiResponse<MonthlyReportResponse>> analyzeReport(
            @Parameter(hidden = true) @RequestAttribute("member") Member member,
            @RequestBody ReportAnalyzeRequest request) {

        MonthlyReportResponse response = monthlyReportService.analyzeAndSaveReport(request, member);
        return ResponseEntity.ok(ApiResponse.onSuccess(MeasurementSuccessCode.REPORT_ANALYZE, response));
    }

    @Operation(summary = "월간리포트 알림 신청 API by 김승연(개발완료)", description = "알림 수신 상태를 설정합니다. (MEASURE: 정기 측정 주기 알림 / RESULT: 리포트 발행 완료 알림)")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "알림 설정 완료",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "알림 등록 성공 예시",
                                    value = "{\"isSuccess\": true, \"code\": \"REPORT200_4\", \"message\": \"정기 알림 설정이 완료되었습니다.\", \"result\": {\"alarm_type\": \"MEASURE\", \"is_alarm_set\": true}}"
                            )
                    )
            )
    })
    @PostMapping("/alarm")
    public ResponseEntity<ApiResponse<AlarmResponse>> registerAlarm(
            @Parameter(hidden = true) @RequestAttribute("member") Member member,
            @RequestBody AlarmRequest request) {

        AlarmResponse response = monthlyReportService.registerAlarm(request, member);
        return ResponseEntity.ok(ApiResponse.onSuccess(MeasurementSuccessCode.REPORT_ALARM_SET, response));
    }
}