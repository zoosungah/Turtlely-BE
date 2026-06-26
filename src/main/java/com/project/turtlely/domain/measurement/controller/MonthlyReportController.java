package com.project.turtlely.domain.measurement.controller;

import com.project.turtlely.domain.measurement.dto.AlarmRequest;
import com.project.turtlely.domain.measurement.dto.AlarmResponse;
import com.project.turtlely.domain.measurement.dto.MonthlyReportResponse;
import com.project.turtlely.domain.measurement.dto.ReportAnalyzeRequest;
import com.project.turtlely.domain.measurement.service.MonthlyReportService;
import com.project.turtlely.domain.member.entity.Member;
import com.project.turtlely.global.apiPayload.ApiResponse;
import com.project.turtlely.global.apiPayload.code.GeneralSuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "월간측정/월간리포트", description = "월간리포트 관련 API")
@RestController
@RequestMapping("/api/monthly")
@RequiredArgsConstructor
public class MonthlyReportController {

    private final MonthlyReportService monthlyReportService;

    @Operation(summary = "월간 리포트 조회 API by 김승연(개발완료)", description = "거북목 유형과 cra, cva 등 월간 리포트를 조회합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "조회 성공 (데이터 존재 및 미측정 케이스)",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "1. 해당 월에 측정 데이터가 존재할 때 (AVAILABLE)",
                                            description = "노션 명세서의 AVAILABLE 성공 케이스 규격입니다.",
                                            value = "{\n" +
                                                    "  \"isSuccess\": true,\n" +
                                                    "  \"code\": \"REPORT_DETAIL_200\",\n" +
                                                    "  \"message\": \"특정 월간 리포트 조회가 완료되었습니다.\",\n" +
                                                    "  \"result\": {\n" +
                                                    "    \"data_status\": \"AVAILABLE\",\n" +
                                                    "    \"monthly_id\": 1,\n" +
                                                    "    \"nickname\": \"turtle\",\n" +
                                                    "    \"posture_type\": \"역C자목\",\n" +
                                                    "    \"score\": 100,\n" +
                                                    "    \"cva_angle\": 48.5,\n" +
                                                    "    \"cra_angle\": 128.34,\n" +
                                                    "    \"cva_history\": [\n" +
                                                    "      { \"month\": \"11월\", \"angle\": 45.2 },\n" +
                                                    "      { \"month\": \"12월\", \"angle\": 46.8 },\n" +
                                                    "      { \"month\": \"1월\", \"angle\": 46.5 },\n" +
                                                    "      { \"month\": \"3월\", \"angle\": 48.0 },\n" +
                                                    "      { \"month\": \"5월\", \"angle\": 47.2 },\n" +
                                                    "      { \"month\": \"6월\", \"angle\": 48.5 }\n" +
                                                    "    ],\n" +
                                                    "    \"cra_history\": [\n" +
                                                    "      { \"month\": \"11월\", \"angle\": 120.1 },\n" +
                                                    "      { \"month\": \"12월\", \"angle\": 124.5 },\n" +
                                                    "      { \"month\": \"1월\", \"angle\": 123.0 },\n" +
                                                    "      { \"month\": \"3월\", \"angle\": 127.8 },\n" +
                                                    "      { \"month\": \"5월\", \"angle\": 125.2 },\n" +
                                                    "      { \"month\": \"6월\", \"angle\": 128.34 }\n" +
                                                    "    ],\n" +
                                                    "    \"alarm_set\": false,\n" +
                                                    "    \"measured_at\": \"2026-06-25T11:00:00\"\n" +
                                                    "  }\n" +
                                                    "}"
                                    ),
                                    @ExampleObject(
                                            name = "2. 해당 월의 정기 측정 기록이 존재하지 않을 때 (NOT_YET)",
                                            description = "노션 명세서의 NOT_YET 성공 케이스 규격입니다.",
                                            value = "{\n" +
                                                    "  \"isSuccess\": true,\n" +
                                                    "  \"code\": \"REPORT_DETAIL_200\",\n" +
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
                                                    "    \"alarm_set\": false,\n" +
                                                    "    \"measured_at\": null\n" +
                                                    "  }\n" +
                                                    "}"
                                    )
                            }
                    )
            )
    })
    @GetMapping("/{monthly_id}")
    public ResponseEntity<ApiResponse<MonthlyReportResponse>> getMonthlyReport(
            @RequestAttribute("member") Member member,
            @PathVariable("monthly_id") Long monthlyId) {

        MonthlyReportResponse response = monthlyReportService.getMonthlyReport(monthlyId, member);

        if ("NOT_YET".equals(response.getDataStatus())) {
            return ResponseEntity.ok(ApiResponse.onSuccess(GeneralSuccessCode.REPORT_NOT_FOUND_200, response));
        }

        return ResponseEntity.ok(ApiResponse.onSuccess(GeneralSuccessCode.REPORT_DETAIL_200, response));
    }

    @Operation(summary = "월간 측정용 프레임 좌표 분석 API by 김승연(개발완료)", description = "프론트엔드에서 수집된 3초간의 프레임 좌표들을 연산하여 거북목 판정 상태를 최종 저장합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "분석 및 저장 성공",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "분석 완료 응답 스펙 (AVAILABLE)",
                                    description = "좌표 분석이 정상 완료되어 리포트가 생성되었을 때의 응답 예시입니다.",
                                    value = "{\n" +
                                            "  \"isSuccess\": true,\n" +
                                            "  \"code\": \"REPORT_DETAIL_200\",\n" +
                                            "  \"message\": \"특정 월간 리포트 조회가 완료되었습니다.\",\n" +
                                            "  \"result\": {\n" +
                                            "    \"data_status\": \"AVAILABLE\",\n" +
                                            "    \"monthly_id\": 1,\n" +
                                            "    \"nickname\": \"turtle\",\n" +
                                            "    \"posture_type\": \"역C자목\",\n" +
                                            "    \"score\": 100,\n" +
                                            "    \"cva_angle\": 48.5,\n" +
                                            "    \"cra_angle\": 128.34,\n" +
                                            "    \"cva_history\": [\n" +
                                            "      { \"month\": \"11월\", \"angle\": 45.2 },\n" +
                                            "      { \"month\": \"12월\", \"angle\": 46.8 },\n" +
                                            "      { \"month\": \"1월\", \"angle\": 46.5 },\n" +
                                            "      { \"month\": \"3월\", \"angle\": 48.0 },\n" +
                                            "      { \"month\": \"5월\", \"angle\": 47.2 },\n" +
                                            "      { \"month\": \"6월\", \"angle\": 48.5 }\n" +
                                            "    ],\n" +
                                            "    \"cra_history\": [\n" +
                                            "      { \"month\": \"11월\", \"angle\": 120.1 },\n" +
                                            "      { \"month\": \"12월\", \"angle\": 124.5 },\n" +
                                            "      { \"month\": \"1월\", \"angle\": 123.0 },\n" +
                                            "      { \"month\": \"3월\", \"angle\": 127.8 },\n" +
                                            "      { \"month\": \"5월\", \"angle\": 125.2 },\n" +
                                            "      { \"month\": \"6월\", \"angle\": 128.34 }\n" +
                                            "    ],\n" +
                                            "    \"alarm_set\": false,\n" +
                                            "    \"measured_at\": \"2026-06-25T11:00:00\"\n" +
                                            "  }\n" +
                                            "}"
                            )
                    )
            )
    })
    @PostMapping("/measurements")
    public ResponseEntity<ApiResponse<MonthlyReportResponse>> analyzeReport(
            @RequestAttribute("member") Member member,
            @RequestBody ReportAnalyzeRequest request) {

        MonthlyReportResponse response = monthlyReportService.analyzeAndSaveReport(request, member);

        return ResponseEntity.ok(ApiResponse.onSuccess(GeneralSuccessCode.REPORT_ANALYZE_200, response));
    }

    @Operation(
            summary = "월간리포트 알림 신청 API by 김승연(개발완료)",
            description = "사용자가 [알림 설정] 버튼을 눌렀을 때 호출되는 알림 신청 API입니다.\n" +
                    "• MEASURE: 아직 거북목 측정 시기가 되지 않았을 때 신청하며, 30일 뒤 측정 푸시 알림이 예약됩니다.\n" +
                    "• RESULT: 측정은 완료했으나 분석 결과 산출 중일 때 신청하며, 리포트 발행 완료 시 결과 푸시 알림이 발송됩니다."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "정기 알림 설정 완료",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "알림 신청 성공 예시",
                                    value = "{\n" +
                                            "  \"isSuccess\": true,\n" +
                                            "  \"code\": \"REPORT_ALARM_SET_200\",\n" +
                                            "  \"message\": \"정기 알림 설정이 완료되었습니다.\",\n" +
                                            "  \"result\": {\n" +
                                            "    \"alarm_type\": \"MEASURE\",\n" +
                                            "    \"is_alarm_set\": true\n" +
                                            "  }\n" +
                                            "}"
                            )
                    )
            )
    })
    @PostMapping("/alarm")
    public ResponseEntity<ApiResponse<AlarmResponse>> registerAlarm(
            @RequestAttribute("member") Member member,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "신청할 알림 유형 (MEASURE 또는 RESULT)",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "기본 요청 예시",
                                    value = "{\n  \"alarm_type\": \"MEASURE\"\n}"
                            )
                    )
            )
            @RequestBody AlarmRequest request) {

        AlarmResponse response = monthlyReportService.registerAlarm(request, member);
        return ResponseEntity.ok(ApiResponse.onSuccess(GeneralSuccessCode.REPORT_ALARM_SET_200, response));
    }
}