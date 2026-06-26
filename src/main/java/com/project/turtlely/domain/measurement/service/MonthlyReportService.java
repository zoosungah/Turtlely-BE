package com.project.turtlely.domain.measurement.service;

import com.project.turtlely.domain.measurement.dto.MonthlyReportResponse;
import com.project.turtlely.domain.measurement.dto.ReportAnalyzeRequest;
import com.project.turtlely.domain.member.entity.Member;

public interface MonthlyReportService {
    MonthlyReportResponse getMonthlyReport(Long monthlyId, Member member);
    MonthlyReportResponse analyzeAndSaveReport(ReportAnalyzeRequest request, Member member);
}