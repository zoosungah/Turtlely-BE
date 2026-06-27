package com.project.turtlely.domain.daily.service;

import com.project.turtlely.domain.daily.dto.DailyResponseDTO;
import com.project.turtlely.domain.daily.entity.DailyReport;
import com.project.turtlely.domain.daily.exception.DailyException;
import com.project.turtlely.domain.daily.exception.code.DailyErrorCode;
import com.project.turtlely.domain.daily.repository.DailyReportRepository;
import com.project.turtlely.domain.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DailyService {
    private final DailyReportRepository dailyReportRepository;

    /**
     * 일일 리포트 상세 조회
     */
    public DailyResponseDTO.DailyReportDTO getDailyReport(Member member, Long dailyId) {
        // 1. 해당 dailyId를 가진 일일 리포트 데이터 조회
        DailyReport dailyReport = dailyReportRepository.findById(dailyId)
                .orElseThrow(() -> new DailyException(DailyErrorCode.REPORT_NOT_FOUND));

        // 2. 조회 권한 확인
        if (!dailyReport.getMemberId().equals(member.getMemberId())) {
            throw new DailyException(DailyErrorCode.REPORT_ACCESS_DENIED);
        }

        // 3. 데이터 반환
        return DailyResponseDTO.DailyReportDTO.builder()
                .postureScore(dailyReport.getTotalScore())
                .averageCva(dailyReport.getAvgAngle())
                .cautionCount(dailyReport.getCautionDuration())
                .warningCount(dailyReport.getWarningDuration())
                .build();
    }
}