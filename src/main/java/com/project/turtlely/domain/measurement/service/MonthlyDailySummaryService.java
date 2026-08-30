package com.project.turtlely.domain.measurement.service;

import com.project.turtlely.domain.daily.entity.DailyReport;
import com.project.turtlely.domain.daily.repository.DailyReportRepository;
import com.project.turtlely.domain.measurement.dto.MonthlyDailySummaryResponseDTO;
import com.project.turtlely.domain.measurement.dto.MonthlyDailySummaryResponseDTO.MonthlyComparisonDTO;
import com.project.turtlely.domain.measurement.dto.MonthlyDailySummaryResponseDTO.WeeklyStatDTO;
import com.project.turtlely.domain.measurement.exception.MeasurementException;
import com.project.turtlely.domain.measurement.exception.code.MeasurementErrorCode;
import com.project.turtlely.domain.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * 월간 리포트 속 일일 측정으로 만든 주간 통계 관련
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MonthlyDailySummaryService {

    private final DailyReportRepository dailyReportRepository;

    public MonthlyDailySummaryResponseDTO.SummaryDTO getMonthlyDailySummary(Member member, int year, int month) {
        if (month < 1 || month > 12 || year < 2000) {
            throw new MeasurementException(MeasurementErrorCode.INVALID_DATE_PARAMETER);
        }

        LocalDate startOfCurrentMonth = LocalDate.of(year, month, 1);
        LocalDate endOfCurrentMonth = startOfCurrentMonth.withDayOfMonth(startOfCurrentMonth.lengthOfMonth());

        List<DailyReport> currentReports = dailyReportRepository.findByMemberIdAndReportDateBetween(
                member.getMemberId(), startOfCurrentMonth, endOfCurrentMonth);

        List<WeeklyStatDTO> weeklyStats = new ArrayList<>();
        for (int week = 1; week <= 4; week++) {
            int startDay = (week - 1) * 7 + 1;
            int endDay = (week == 4) ? startOfCurrentMonth.lengthOfMonth() : week * 7;

            List<DailyReport> weekReports = currentReports.stream()
                    .filter(r -> r.getReportDate().getDayOfMonth() >= startDay && r.getReportDate().getDayOfMonth() <= endDay)
                    .toList();

            weeklyStats.add(calculateWeeklyStat(week, weekReports));
        }

        LocalDate startOfPrevMonth = startOfCurrentMonth.minusMonths(1);
        LocalDate endOfPrevMonth = startOfPrevMonth.withDayOfMonth(startOfPrevMonth.lengthOfMonth());
        List<DailyReport> prevReports = dailyReportRepository.findByMemberIdAndReportDateBetween(
                member.getMemberId(), startOfPrevMonth, endOfPrevMonth);

        MonthlyComparisonDTO comparison = calculateComparison(currentReports, prevReports);

        return MonthlyDailySummaryResponseDTO.SummaryDTO.builder()
                .year(year)
                .month(month)
                .weeklyStats(weeklyStats)
                .monthlyComparison(comparison)
                .build();
    }

    private WeeklyStatDTO calculateWeeklyStat(int week, List<DailyReport> reports) {
        int totalDuration = reports.stream().mapToInt(DailyReport::getTotalMeasurementDuration).sum();
        if (totalDuration == 0) {
            return WeeklyStatDTO.builder()
                    .week(week)
                    .weekLabel(week + "주차")
                    .averageCva(0.0)
                    .normalRatio(0)
                    .cautionRatio(0)
                    .warningRatio(0)
                    .hasData(false)
                    .build();
        }

        int normalSec = reports.stream().mapToInt(DailyReport::getNormalDuration).sum();
        int cautionSec = reports.stream().mapToInt(DailyReport::getCautionDuration).sum();
        int warningSec = reports.stream().mapToInt(DailyReport::getWarningDuration).sum();
        double cvaSum = reports.stream().mapToDouble(DailyReport::getCvaSum).sum();

        return WeeklyStatDTO.builder()
                .week(week)
                .weekLabel(week + "주차")
                .averageCva(Math.round((cvaSum / totalDuration) * 10.0) / 10.0)
                .normalRatio((int) Math.round(((double) normalSec / totalDuration) * 100))
                .cautionRatio((int) Math.round(((double) cautionSec / totalDuration) * 100))
                .warningRatio((int) Math.round(((double) warningSec / totalDuration) * 100))
                .hasData(true)
                .build();
    }

    private MonthlyComparisonDTO calculateComparison(List<DailyReport> current, List<DailyReport> prev) {
        int curTotal = current.stream().mapToInt(DailyReport::getTotalMeasurementDuration).sum();
        int prevTotal = prev.stream().mapToInt(DailyReport::getTotalMeasurementDuration).sum();

        if (curTotal == 0 || prevTotal == 0) {
            return MonthlyComparisonDTO.builder()
                    .normalRatioDiff(0)
                    .cautionRatioDiff(0)
                    .warningRatioDiff(0)
                    .cvaDiff(0.0)
                    .build();
        }

        int curNormal = (int) Math.round(((double) current.stream().mapToInt(DailyReport::getNormalDuration).sum() / curTotal) * 100);
        int prevNormal = (int) Math.round(((double) prev.stream().mapToInt(DailyReport::getNormalDuration).sum() / prevTotal) * 100);

        int curCaution = (int) Math.round(((double) current.stream().mapToInt(DailyReport::getCautionDuration).sum() / curTotal) * 100);
        int prevCaution = (int) Math.round(((double) prev.stream().mapToInt(DailyReport::getCautionDuration).sum() / prevTotal) * 100);

        int curWarning = (int) Math.round(((double) current.stream().mapToInt(DailyReport::getWarningDuration).sum() / curTotal) * 100);
        int prevWarning = (int) Math.round(((double) prev.stream().mapToInt(DailyReport::getWarningDuration).sum() / prevTotal) * 100);

        double curCva = current.stream().mapToDouble(DailyReport::getCvaSum).sum() / curTotal;
        double prevCva = prev.stream().mapToDouble(DailyReport::getCvaSum).sum() / prevTotal;

        return MonthlyComparisonDTO.builder()
                .normalRatioDiff(curNormal - prevNormal)
                .cautionRatioDiff(curCaution - prevCaution)
                .warningRatioDiff(curWarning - prevWarning)
                .cvaDiff(Math.round((curCva - prevCva) * 10.0) / 10.0)
                .build();
    }
}