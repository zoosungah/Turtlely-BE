package com.project.turtlely.domain.measurement.scheduler;

import com.project.turtlely.domain.measurement.service.MonthlyReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DailyAlarmCleanScheduler {

    private final MonthlyReportService monthlyReportService;

    @Scheduled(cron = "0 0 2 * * *")
    public void cleanExpiredAlarmsDaily() {
        monthlyReportService.expireExpiredAlarms();
        monthlyReportService.createMonthlyMeasurementAlerts(); //측정 30일 지났을 때 알림 생성
    }
}