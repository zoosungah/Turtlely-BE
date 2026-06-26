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
                                                    "  \"code\": \"REPORT_DETAIL_200\",\n" +
                                                    "  \"message\": \"특정 월간 리포트 조회가 완료되었습니다.\",\n" +
                                                    "  \"result\": {\n" +
                                                    "    \"cra_angle\": 0,\n" +
                                                    "    \"cra_history\": [{\"angle\": 0, \"month\": \"6월\"}],\n" +
                                                    "    \"cva_angle\": 0,\n" +
                                                    "    \"cva_history\": [{\"angle\": 0, \"month\": \"6월\"}],\n" +
                                                    "    \"data_status\": \"AVAILABLE\",\n" +
                                                    "    \"general_opinion\": \"현재 측정된 CVA와 CRA가 0.00도로 표시되어 실제 자세 평가값이 정상적으로 반영되지 않았을 가능성이 크지만... 정확한 재평가를 받는 것이 좋습니다.\",\n" +
                                                    "    \"measured_at\": \"2026-06-26T20:53:11.1171978\",\n" +
                                                    "    \"measurement_alarm\": true,\n" +
                                                    "    \"monthly_id\": 16,\n" +
                                                    "    \"nickname\": \"seungyeon\",\n" +
                                                    "    \"posture_type\": \"역C자목\",\n" +
                                                    "    \"prediction_graph\": [\n" +
                                                    "      {\"angle\": 0, \"month\": \"현재월\"},\n" +
                                                    "      {\"angle\": 1, \"month\": \"1개월 후\"},\n" +
                                                    "      {\"angle\": 1, \"month\": \"2개월 후\"},\n" +
                                                    "      {\"angle\": 2, \"month\": \"3개월 후\"},\n" +
                                                    "      {\"angle\": 2, \"month\": \"4개월 후\"},\n" +
                                                    "      {\"angle\": 3, \"month\": \"5개월 후\"}\n" +
                                                    "    ],\n" +
                                                    "    \"report_alarm\": true,\n" +
                                                    "    \"score\": 40,\n" +
                                                    "    \"top3_diseases\": [\"목디스크\", \"근막통증증후군\", \"경추성 두통\"]\n" +
                                                    "  }\n" +
                                                    "}"
                                    ),
                                    @ExampleObject(
                                            name = "2. 해당 월의 정기 측정 기록이 존재하지 않을 때 (NOT_YET)",
                                            value = "{\n" +
                                                    "  \"isSuccess\": true,\n" +
                                                    "  \"code\": \"REPORT_DETAIL_200\",\n" +
                                                    "  \"message\": \"해당 월의 정기 측정 기록이 존재하지 않습니다.\",\n" +
                                                    "  \"result\": {\n" +
                                                    "    \"cra_angle\": null,\n" +
                                                    "    \"cra_history\": [],\n" +
                                                    "    \"cva_angle\": null,\n" +
                                                    "    \"cva_history\": [],\n" +
                                                    "    \"data_status\": \"NOT_YET\",\n" +
                                                    "    \"general_opinion\": null,\n" +
                                                    "    \"measured_at\": null,\n" +
                                                    "    \"measurement_alarm\": false,\n" +
                                                    "    \"monthly_id\": null,\n" +
                                                    "    \"nickname\": \"turtle\",\n" +
                                                    "    \"posture_type\": \"데이터 없음\",\n" +
                                                    "    \"prediction_graph\": [],\n" +
                                                    "    \"report_alarm\": false,\n" +
                                                    "    \"score\": null,\n" +
                                                    "    \"top3_diseases\": []\n" +
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
            return ResponseEntity.ok(ApiResponse.onSuccess(MeasurementSuccessCode.REPORT_NOT_FOUND_200, response));
        }

        return ResponseEntity.ok(ApiResponse.onSuccess(MeasurementSuccessCode.REPORT_DETAIL_200, response));
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
                                            "  \"code\": \"REPORT_ANALYZE_200\",\n" +
                                            "  \"message\": \"특정 월간 리포트 분석 및 저장이 완료되었습니다.\",\n" +
                                            "  \"result\": {\n" +
                                            "    \"cra_angle\": 0,\n" +
                                            "    \"cra_history\": [{\"angle\": 0, \"month\": \"6월\"}],\n" +
                                            "    \"cva_angle\": 0,\n" +
                                            "    \"cva_history\": [{\"angle\": 0, \"month\": \"6월\"}],\n" +
                                            "    \"data_status\": \"AVAILABLE\",\n" +
                                            "    \"general_opinion\": \"현재 측정된 CVA와 CRA가 0.00도로 표시되어 실제 자세 평가값이 정상적으로 반영되지 않았을 가능성이 크지만... 정확한 재평가를 받는 것이 좋습니다.\",\n" +
                                            "    \"measured_at\": \"2026-06-26T20:53:11.1171978\",\n" +
                                            "    \"measurement_alarm\": true,\n" +
                                            "    \"monthly_id\": 16,\n" +
                                            "    \"nickname\": \"seungyeon\",\n" +
                                            "    \"posture_type\": \"역C자목\",\n" +
                                            "    \"prediction_graph\": [\n" +
                                            "      {\"angle\": 0, \"month\": \"현재월\"},\n" +
                                            "      {\"angle\": 1, \"month\": \"1개월 후\"},\n" +
                                            "      {\"angle\": 1, \"month\": \"2개월 후\"},\n" +
                                            "      {\"angle\": 2, \"month\": \"3개월 후\"},\n" +
                                            "      {\"angle\": 2, \"month\": \"4개월 후\"},\n" +
                                            "      {\"angle\": 3, \"month\": \"5개월 후\"}\n" +
                                            "    ],\n" +
                                            "    \"report_alarm\": true,\n" +
                                            "    \"score\": 40,\n" +
                                            "    \"top3_diseases\": [\"목디스크\", \"근막통증증후군\", \"경추성 두통\"]\n" +
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
        return ResponseEntity.ok(ApiResponse.onSuccess(MeasurementSuccessCode.REPORT_ANALYZE_200, response));
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
                                    value = "{\"isSuccess\": true, \"code\": \"REPORT_ALARM_SET_200\", \"message\": \"정기 알림 설정이 완료되었습니다.\", \"result\": {\"alarm_type\": \"MEASURE\", \"is_alarm_set\": true}}"
                            )
                    )
            )
    })
    @PostMapping("/alarm")
    public ResponseEntity<ApiResponse<AlarmResponse>> registerAlarm(
            @Parameter(hidden = true) @RequestAttribute("member") Member member,
            @RequestBody AlarmRequest request) {

        AlarmResponse response = monthlyReportService.registerAlarm(request, member);
        return ResponseEntity.ok(ApiResponse.onSuccess(MeasurementSuccessCode.REPORT_ALARM_SET_200, response));
    }
}