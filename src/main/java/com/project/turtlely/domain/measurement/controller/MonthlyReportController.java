package com.project.turtlely.domain.measurement.controller;

import com.project.turtlely.domain.measurement.dto.AlarmRequest;
import com.project.turtlely.domain.measurement.dto.AlarmResponse;
import com.project.turtlely.domain.measurement.dto.MonthlyReportResponse;
import com.project.turtlely.domain.measurement.dto.ReportAnalyzeRequest;
import com.project.turtlely.domain.measurement.service.MonthlyReportService;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "월간측정/월간리포트")
@RestController
@RequestMapping("/api/monthly")
@RequiredArgsConstructor
@SecurityRequirement(name = "JWT_TOKEN")
public class MonthlyReportController {

    private final MonthlyReportService monthlyReportService;

    @Operation(summary = "유저별 월간 리포트 목록 조회 API by 김승연(개발완료)", description = "유저가 가진 월간 리포트 목록을 최신순으로 조회하며, 동일 월은 최신 데이터만 반환합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "목록 조회 성공 명세",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "월 목록 성공 반환 명세",
                                    value = "[\n" +
                                            "  {\n" +
                                            "    \"monthly_id\": 18,\n" +
                                            "    \"report_year\": 2026,\n" +
                                            "    \"report_month\": 7,\n" +
                                            "    \"measured_at\": \"2026-07-02T17:15:00\"\n" +
                                            "  },\n" +
                                            "  {\n" +
                                            "    \"monthly_id\": 14,\n" +
                                            "    \"report_year\": 2026,\n" +
                                            "    \"report_month\": 6,\n" +
                                            "    \"measured_at\": \"2026-06-25T14:30:00\"\n" +
                                            "  }\n" +
                                            "]"
                            )
                    )
            )
    })
    @GetMapping("/list")
    public ResponseEntity<ApiResponse<List<MonthlyReportResponse.MonthlyReportListResponse>>> getMonthlyReportList(
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails) {

        List<MonthlyReportResponse.MonthlyReportListResponse> response = monthlyReportService.getMonthlyReportList(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.onSuccess(MeasurementSuccessCode.REPORT_DETAIL, response));
    }

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
                                                    "    \"posture_type\": \"주의\",\n" +
                                                    "    \"score\": 73,\n" +
                                                    "    \"cva_angle\": 45.5,\n" +
                                                    "    \"cra_angle\": 143.2,\n" +
                                                    "    \"cva_history\": [{\"month\": \"5월\", \"angle\": 42.1}, {\"month\": \"6월\", \"angle\": 45.5}],\n" +
                                                    "    \"cra_history\": [{\"month\": \"5월\", \"angle\": 138.4}, {\"month\": \"6월\", \"angle\": 143.2}],\n" +
                                                    "    \"measurement_alarm\": true,\n" +
                                                    "    \"report_alarm\": true,\n" +
                                                    "    \"measured_at\": \"2026-06-26T20:53:11\",\n" +
                                                    "    \"report_year\": 2026,\n" +
                                                    "    \"report_month\": 7,\n" +
                                                    "    \"predicted_diseases\": [\n" +
                                                    "      { \"name\": \"목디스크\", \"score\": 0.85 },\n" +
                                                    "      { \"name\": \"후두신경통\", \"score\": 0.65 },\n" +
                                                    "      { \"name\": \"척추측만증\", \"score\": 0.35 }\n" +
                                                    "    ],\n" +
                                                    "    \"prediction_data\": {\n" +
                                                    "      \"prediction_months\": [\"6월\", \"7월\", \"8월\", \"9월\", \"10월\", \"11월\"],\n" +
                                                    "      \"prediction_scores\": [70, 72, 75, 78, 82, 85]\n" +
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
                                                    "    \"report_year\": 2026,\n" +
                                                    "    \"report_month\": 7,\n" +
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
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "리포트 고유 ID", example = "1") @PathVariable("monthly_id") Long monthlyId) {

        MonthlyReportResponse response = monthlyReportService.getMonthlyReport(monthlyId, userDetails.getUsername());

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
                                    value = "{\n" +
                                            "  \"isSuccess\": true,\n" +
                                            "  \"code\": \"REPORT200_3\",\n" +
                                            "  \"message\": \"특정 월간 리포트 분석 및 저장이 완료되었습니다.\",\n" +
                                            "  \"result\": {\n" +
                                            "    \"data_status\": \"AVAILABLE\",\n" +
                                            "    \"monthly_id\": 16,\n" +
                                            "    \"nickname\": \"seungyeon\",\n" +
                                            "    \"posture_type\": \"위험\",\n" +
                                            "    \"score\": 73,\n" +
                                            "    \"cva_angle\": 41.2,\n" +
                                            "    \"cra_angle\": 145.0,\n" +
                                            "    \"cva_history\": [{\"month\": \"6월\", \"angle\": 41.2}],\n" +
                                            "    \"cra_history\": [{\"month\": \"6월\", \"angle\": 145.0}],\n" +
                                            "    \"measurement_alarm\": true,\n" +
                                            "    \"report_alarm\": true,\n" +
                                            "    \"measured_at\": \"2026-06-26T20:53:11\",\n" +
                                            "    \"report_year\": 2026,\n" +
                                            "    \"report_month\": 7,\n" +
                                            "    \"predicted_diseases\": [\n" +
                                            "      { \"name\": \"목디스크\", \"score\": 0.85 },\n" +
                                            "      { \"name\": \"후두신경통\", \"score\": 0.65 },\n" +
                                            "      { \"name\": \"척추측만증\", \"score\": 0.35 }\n" +
                                            "    ],\n" +
                                            "    \"prediction_data\": {\n" +
                                            "      \"prediction_months\": [\"6월\", \"7월\", \"8월\", \"9월\", \"10월\", \"11월\"],\n" +
                                            "      \"prediction_scores\": [40, 42, 45, 50, 55, 60]\n" +
                                            "    }\n" +
                                            "  }\n" +
                                            "}"
                            )
                    )
            )
    })
    @PostMapping("/measurements")
    public ResponseEntity<ApiResponse<MonthlyReportResponse>> analyzeReport(
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody ReportAnalyzeRequest request) {

        MonthlyReportResponse response = monthlyReportService.analyzeAndSaveReport(request, userDetails.getUsername());
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
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody AlarmRequest request) {

        AlarmResponse response = monthlyReportService.registerAlarm(request, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.onSuccess(MeasurementSuccessCode.REPORT_ALARM_SET, response));
    }
}