package com.project.turtlely.domain.measurement.service;

import com.project.turtlely.domain.measurement.dto.AlarmRequest;
import com.project.turtlely.domain.measurement.dto.AlarmResponse;
import com.project.turtlely.domain.measurement.dto.MonthlyReportResponse;
import com.project.turtlely.domain.measurement.dto.ReportAnalyzeRequest;

public interface MonthlyReportService {

    MonthlyReportResponse getMonthlyReport(Long monthlyId, String loginId);

    MonthlyReportResponse analyzeAndSaveReport(ReportAnalyzeRequest request, String loginId);

    AlarmResponse registerAlarm(AlarmRequest request, String loginId);
}