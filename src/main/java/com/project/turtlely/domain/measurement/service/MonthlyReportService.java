package com.project.turtlely.domain.measurement.service;

import com.project.turtlely.domain.measurement.dto.AlarmRequest;
import com.project.turtlely.domain.measurement.dto.AlarmResponse;
import com.project.turtlely.domain.measurement.dto.MonthlyReportResponse;
import com.project.turtlely.domain.measurement.dto.ReportAnalyzeRequest;

import java.util.List;

public interface MonthlyReportService {

    // 월간 리포트 종합 조회
    MonthlyReportResponse getMonthlyReport(Long monthlyId,String loginId);
    // 프레임 데이터 분석, CVA/CRA 각도 연산 및 GPT 결과 저장
    MonthlyReportResponse analyzeAndSaveReport(ReportAnalyzeRequest request, String loginId);
    // 정기 측정 주기(MEASURE) 또는 리포트 발행(RESULT) 알림 신청
    AlarmResponse registerAlarm(AlarmRequest request, String loginId);
    void expireExpiredAlarms();
    List<MonthlyReportResponse.MonthlyReportListResponse> getMonthlyReportList(String loginId);
}