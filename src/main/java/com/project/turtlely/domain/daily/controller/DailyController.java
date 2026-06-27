package com.project.turtlely.domain.daily.controller;

import com.project.turtlely.domain.daily.dto.DailyResponseDTO;
import com.project.turtlely.domain.daily.exception.code.DailySuccessCode;
import com.project.turtlely.domain.daily.service.DailyService;
import com.project.turtlely.domain.member.service.PrincipalDetails;
import com.project.turtlely.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "일일 리포트 관리", description = "일일 리포트 상세 데이터 조회 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class DailyController {

    private final DailyService dailyService;

    /**
     * 일일 리포트 상세 조회 API
     */
    @Operation(
            summary = "일일 리포트 상세 조회 API by 주성아(개발 완료)",
            description = "리포트ID(dailyId)를 경로로 받아 해당 일자의 자세 유지 점수, 평균 CVA 각도, 주의/경고 횟수를 반환합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "REPORT200_1",
                    description = "일일 리포트 조회에 성공하였습니다."
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "REPORT404_1",
                    description = "해당 ID의 리포트 데이터가 존재하지 않습니다."
            )
    })
    @GetMapping("/daily/{dailyId}")
    public ApiResponse<DailyResponseDTO.DailyReportDTO> getDailyReport(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @PathVariable Long dailyId) {

        DailyResponseDTO.DailyReportDTO result = dailyService.getDailyReport(principalDetails.getMember(), dailyId);
        return ApiResponse.onSuccess(DailySuccessCode.REPORT_GET_SUCCESS, result);
    }
}