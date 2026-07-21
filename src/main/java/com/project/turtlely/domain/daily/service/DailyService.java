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

import java.util.List;

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

        int calculatedScore = calculateTotalScore(
                dailyReport.getCautionDuration(),
                dailyReport.getWarningDuration(),
                dailyReport.getTotalMeasurementDuration(),
                dailyReport.getTotalScore() // Fallback용 기존 점수
        );

        // 3. 데이터 반환
        return DailyResponseDTO.DailyReportDTO.builder()
                .postureScore(dailyReport.getTotalScore())
                .averageCva(dailyReport.getAvgAngle())
                .cautionCount(dailyReport.getCautionDuration())
                .warningCount(dailyReport.getWarningDuration())
                .build();
    }

    private int calculateTotalScore(int cautionDuration, int warningDuration, int totalDuration, int defaultScore) {
        // 전체 측정 시간이 없거나 데이터가 유효하지 않은 경우 예외 처리 또는 기존 점수 반환
        if (totalDuration <= 0) {
            return defaultScore > 0 ? defaultScore : 0;
        }

        // 1. 가중치가 적용된 페널티 초 계산
        double penaltySeconds = (cautionDuration * 0.4) + (warningDuration * 1.0);

        // 2. 전체 측정 시간 대비 페널티 비율 환산 후 100점 만점 적용
        double penaltyRatio = penaltySeconds / totalDuration * 100;
        int finalScore = (int) Math.round(100 - penaltyRatio);

        // 3. 스코어 바운더리 체크 (0점 ~ 100점 제한)
        return Math.max(0, Math.min(100, finalScore));
    }

    /**
     * 전체 캘린더 기록 조회
     */
    public DailyResponseDTO.CalendarListDTO getAllCalendarReports(Member member) {
        // 1. DB에서 해당 유저의 모든 리포트를 날짜 순으로 가져옴
        List<DailyReport> reports = dailyReportRepository.findByMemberIdOrderByReportDateAsc(member.getMemberId());

        // 2. 엔티티 리스트를 피그마 규격 DTO 리스트로 변환
        List<DailyResponseDTO.CalendarReportDTO> reportDTOs = reports.stream()
                .map(report -> DailyResponseDTO.CalendarReportDTO.builder()
                        .dailyId(report.getDailyReportId())
                        .totalScore(report.getTotalScore())
                        .reportDate(report.getReportDate().toString())
                        .hasReport(true) // 데이터가 있는 날이므로 프론트 표시용 true
                        .build())
                .toList();

        // 3. 리스트 래퍼 DTO로 감싸서 리턴
        return DailyResponseDTO.CalendarListDTO.builder()
                .calendarReports(reportDTOs)
                .build();
    }
}