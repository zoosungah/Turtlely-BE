package com.project.turtlely.domain.measurement.service;

import com.project.turtlely.domain.exercise.entity.VideoLog;
import com.project.turtlely.domain.exercise.repository.VideoLogRepository;
import com.project.turtlely.domain.measurement.dto.*;
import com.project.turtlely.domain.measurement.dto.ReportAnalyzeRequest.FrameData;
import com.project.turtlely.domain.measurement.entity.MonthlyMeasurement;
import com.project.turtlely.domain.measurement.exception.MeasurementErrorCode;
import com.project.turtlely.domain.measurement.exception.MeasurementErrorCode.MeasurementCustomException;
import com.project.turtlely.domain.measurement.repository.MonthlyMeasurementRepository;
import com.project.turtlely.domain.member.entity.Member;
import com.project.turtlely.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MonthlyReportServiceImpl implements MonthlyReportService {

    private final MonthlyMeasurementRepository measurementRepository;
    private final MemberRepository memberRepository;
    private final VideoLogRepository videoLogRepository;
    private final GptService gptService;
    private final com.project.turtlely.domain.notification.repository.NotificationRepository notificationRepository;

    private final MonthlyHWService monthlyHWService;

    @Override
    public MonthlyReportResponse getMonthlyReport(Long monthlyId, String loginId) {
        if (monthlyId == null || monthlyId <= 0) {
            throw new MeasurementCustomException(MeasurementErrorCode.INVALID_REPORT_ID);
        }

        Member latestMember = memberRepository.findByLoginId(loginId)
                .orElseThrow(() -> new IllegalArgumentException("MEMBER_NOT_FOUND"));

        MonthlyMeasurement currentMeasurement = measurementRepository.findByMonthlyIdAndMember(monthlyId, latestMember).orElse(null);

        // 1. 해당 ID의 측정 데이터 기록이 존재하지 않을 때 (NOT_YET)
        if (currentMeasurement == null) {
            return MonthlyReportResponse.builder()
                    .dataStatus("NOT_YET")
                    .monthlyId(null)
                    .nickname(latestMember.getNickname())
                    .postureType("데이터 없음")
                    .cvaAngle(null)
                    .craAngle(null)
                    .cvaHistory(new ArrayList<>())
                    .craHistory(new ArrayList<>())
                    .measurementAlarm(latestMember.isMeasurementAlarm())
                    .reportAlarm(latestMember.isReportAlarm())
                    .measuredAt(null)
                    .predictedDiseases(new ArrayList<>())
                    .predictionData(MonthlyReportResponse.PredictionDataDto.builder()
                            .predictionMonths(new ArrayList<>())
                            .predictionScores(new ArrayList<>())
                            .build())
                    .reportYear(LocalDateTime.now().getYear())
                    .reportMonth(LocalDateTime.now().getMonthValue())
                    .build();
        }

        List<MonthlyMeasurement> rawHistory = measurementRepository.findTop6ByMemberOrderByMeasuredAtDesc(latestMember);

        List<MonthlyMeasurement> chronologicalHistory = new ArrayList<>(rawHistory);
        Collections.reverse(chronologicalHistory);

        List<MonthlyReportResponse.HistoryDto> cvaHistory = new ArrayList<>(chronologicalHistory.stream()
                .map(h -> MonthlyReportResponse.HistoryDto.builder()
                        .month(h.getMeasuredAt().getMonthValue() + "월")
                        .angle((double) h.getCvaAngle())
                        .build())
                .collect(Collectors.toMap(
                        MonthlyReportResponse.HistoryDto::getMonth,
                        dto -> dto,
                        (existing, replacement) -> replacement
                ))
                .values());

        List<MonthlyReportResponse.HistoryDto> craHistory = new ArrayList<>(chronologicalHistory.stream()
                .map(h -> MonthlyReportResponse.HistoryDto.builder()
                        .month(h.getMeasuredAt().getMonthValue() + "월")
                        .angle((double) h.getCraAngle())
                        .build())
                .collect(Collectors.toMap(
                        MonthlyReportResponse.HistoryDto::getMonth,
                        dto -> dto,
                        (existing, replacement) -> replacement
                ))
                .values());

        return MonthlyReportResponse.builder()
                .dataStatus("AVAILABLE")
                .monthlyId(currentMeasurement.getMonthlyId())
                .nickname(latestMember.getNickname())
                .postureType(currentMeasurement.getPostureType())
                .score(currentMeasurement.getScore())
                .cvaAngle((double) currentMeasurement.getCvaAngle())
                .craAngle((double) currentMeasurement.getCraAngle())
                .cvaHistory(cvaHistory)
                .craHistory(craHistory)
                .measurementAlarm(latestMember.isMeasurementAlarm())
                .reportAlarm(latestMember.isReportAlarm())
                .measuredAt(currentMeasurement.getMeasuredAt())
                .predictedDiseases(currentMeasurement.getPredictedDiseasesList())
                .predictionData(MonthlyReportResponse.PredictionDataDto.builder()
                        .predictionMonths(currentMeasurement.getPredictionMonthsList())
                        .predictionScores(currentMeasurement.getPredictionScoresList())
                        .build())
                .reportYear(currentMeasurement.getMeasuredAt().getYear())
                .reportMonth(currentMeasurement.getMeasuredAt().getMonthValue())
                .build();
    }

    @Override
    @Transactional
    public MonthlyReportResponse analyzeAndSaveReport(ReportAnalyzeRequest request, String loginId) {
        if (request.getFrames() == null || request.getFrames().isEmpty()) {
            throw new MeasurementCustomException(MeasurementErrorCode.INVALID_FRAME_DATA);
        }

        Member latestMember = memberRepository.findByLoginId(loginId)
                .orElseThrow(() -> new IllegalArgumentException("MEMBER_NOT_FOUND"));

        FrameData bestFrame = null;
        double minScore = Double.MAX_VALUE;
        FrameData prevFrame = null;

        for (int i = 0; i < request.getFrames().size(); i++) {
            FrameData current = request.getFrames().get(i);

            if (current.getEyeX() <= 0 || current.getEyeX() > 1.0 || current.getEyeY() <= 0 || current.getEyeY() > 1.0 ||
                    current.getTragusX() <= 0 || current.getTragusX() > 1.0 || current.getTragusY() <= 0 || current.getTragusY() > 1.0 ||
                    current.getC7X() <= 0 || current.getC7X() > 1.0 || current.getC7Y() <= 0 || current.getC7Y() > 1.0) {
                continue;
            }

            double movement = 0.0;
            if (i > 0 && prevFrame != null) {
                movement = Math.sqrt(
                        Math.pow(current.getTragusX() - prevFrame.getTragusX(), 2) +
                                Math.pow(current.getTragusY() - prevFrame.getTragusY(), 2) +
                                Math.pow(current.getC7X() - prevFrame.getC7X(), 2) +
                                Math.pow(current.getC7Y() - prevFrame.getC7Y(), 2)
                );
            }

            double centerDistance = Math.sqrt(
                    Math.pow(current.getTragusX() - 0.5, 2) +
                            Math.pow(current.getTragusY() - 0.5, 2)
            );

            double calculatedScore = (movement * 0.7) + (centerDistance * 0.3);

            if (calculatedScore < minScore) {
                minScore = calculatedScore;
                bestFrame = current;
            }
            prevFrame = current;
        }

        if (bestFrame == null) {
            throw new MeasurementCustomException(MeasurementErrorCode.LANDMARK_NOT_FOUND);
        }

        double deltaYCva = Math.abs(bestFrame.getC7Y() - bestFrame.getTragusY());
        double deltaXCva = Math.abs(bestFrame.getC7X() - bestFrame.getTragusX());
        double cvaAngle = Math.toDegrees(Math.atan2(deltaYCva, deltaXCva));

        double[] v1 = { bestFrame.getEyeX() - bestFrame.getTragusX(), bestFrame.getEyeY() - bestFrame.getTragusY() };
        double[] v2 = { bestFrame.getC7X() - bestFrame.getTragusX(), bestFrame.getC7Y() - bestFrame.getTragusY() };

        double dotProd = (v1[0] * v2[0]) + (v1[1] * v2[1]);
        double mag1 = Math.sqrt(Math.pow(v1[0], 2) + Math.pow(v1[1], 2));
        double mag2 = Math.sqrt(Math.pow(v2[0], 2) + Math.pow(v2[1], 2));

        double craAngle = 0.0;
        if (mag1 * mag2 != 0) {
            double cosTheta = dotProd / (mag1 * mag2);
            cosTheta = Math.max(-1.0, Math.min(1.0, cosTheta));
            craAngle = Math.toDegrees(Math.acos(cosTheta));
        }

        String postureType;
        double craDeviation = Math.abs(craAngle - 145.0);

        if (cvaAngle >= 48.7) {
            if (craDeviation <= 10.0) {
                postureType = "정상";
            } else {
                postureType = "주의";
            }
        } else if (cvaAngle < 43.8 || craDeviation > 20.0) {
            postureType = "위험";
        } else {
            postureType = "주의";
        }

        float calibrationC = monthlyHWService.calculateCalibrationC(
                cvaAngle,
                bestFrame.getHwAccelX(),
                bestFrame.getHwAccelY(),
                bestFrame.getHwAccelZ()
        );

        LocalDateTime threeMonthsAgo = LocalDateTime.now().minusMonths(3);
        List<VideoLog> recentLogs = videoLogRepository.findRecentWatchLogs(latestMember.getMemberId(), threeMonthsAgo);
        int totalWatchTimeMinutes = recentLogs.stream().mapToInt(VideoLog::getWatchTime).sum() / 60;

        GptAnalysisResponse gptResult = gptService.requestPostureAnalysis(
                cvaAngle,
                craAngle,
                postureType,
                totalWatchTimeMinutes
        );

        String diseasesText = gptResult.getTop3Diseases().stream()
                .map(d -> d.getName() + ":" + String.format("%.2f", d.getScore()))
                .collect(Collectors.joining(","));

        String predMonths = gptResult.getPredictionGraph().stream()
                .map(p -> p.getMonth())
                .collect(Collectors.joining(","));

        String predScores = gptResult.getPredictionGraph().stream()
                .map(p -> String.valueOf((int) Math.round(p.getAngle())))
                .collect(Collectors.joining(","));

        String finalPredictionData = predMonths + "|" + predScores;

        MonthlyMeasurement measurement = MonthlyMeasurement.builder()
                .member(latestMember)
                .cvaAngle((float) cvaAngle)
                .craAngle((float) craAngle)
                .postureType(postureType)
                .score(gptResult.getCervicalHealthScore())
                .predictedDiseases(diseasesText)
                .predictionData(finalPredictionData)
                .measuredAt(LocalDateTime.now())

                .hwAccelX((float) bestFrame.getHwAccelX())
                .hwAccelY((float) bestFrame.getHwAccelY())
                .hwAccelZ((float) bestFrame.getHwAccelZ())
                .calibrationC(calibrationC)
                .build();

        MonthlyMeasurement saved = measurementRepository.save(measurement);
        measurementRepository.flush();

        return getMonthlyReport(saved.getMonthlyId(), latestMember.getLoginId());
    }

    @Override
    @Transactional
    public AlarmResponse registerAlarm(AlarmRequest request, String loginId) {
        String type = request.getAlarmType();

        if (!"MEASURE".equals(type) && !"RESULT".equals(type)) {
            throw new MeasurementCustomException(MeasurementErrorCode.INVALID_ALARM_TYPE);
        }

        Member latestMember = memberRepository.findByLoginId(loginId)
                .orElseThrow(() -> new IllegalArgumentException("MEMBER_NOT_FOUND"));

        if ("MEASURE".equals(type) && latestMember.isMeasurementAlarm()) {
            throw new MeasurementCustomException(MeasurementErrorCode.ALREADY_ALARM_SET);
        }
        if ("RESULT".equals(type) && latestMember.isReportAlarm()) {
            throw new MeasurementCustomException(MeasurementErrorCode.ALREADY_ALARM_SET);
        }

        try {
            if ("MEASURE".equals(type)) {
                latestMember.updateMeasurementAlarm(true);
            } else {
                latestMember.updateReportAlarm(true);
            }
            memberRepository.saveAndFlush(latestMember);
        } catch (Exception e) {
            throw new MeasurementCustomException(MeasurementErrorCode.SERVER_ERROR);
        }

        return AlarmResponse.builder()
                .alarmType(type)
                .alarmSet(true)
                .build();
    }

    @Override
    @Transactional
    public void expireExpiredAlarms() {
        LocalDateTime oneMonthAgo = LocalDateTime.now().minusMonths(1);

        List<Member> expiredMeasurements = memberRepository.findByIsMeasurementAlarmTrueAndMeasurementAlarmSetAtBefore(oneMonthAgo);
        for (Member member : expiredMeasurements) {
            member.updateMeasurementAlarm(false);
        }

        List<Member> expiredReports = memberRepository.findByIsReportAlarmTrueAndReportAlarmSetAtBefore(oneMonthAgo);
        for (Member member : expiredReports) {
            member.updateReportAlarm(false);
        }

        if (!expiredMeasurements.isEmpty()) {
            memberRepository.saveAll(expiredMeasurements);
        }
        if (!expiredReports.isEmpty()) {
            memberRepository.saveAll(expiredReports);
        }
        memberRepository.flush();
    }

    @Override
    public List<MonthlyReportResponse.MonthlyReportListResponse> getMonthlyReportList(String loginId) {
        Member latestMember = memberRepository.findByLoginId(loginId)
                .orElseThrow(() -> new IllegalArgumentException("MEMBER_NOT_FOUND"));

        List<MonthlyMeasurement> allMeasurements = measurementRepository.findByMemberOrderByMeasuredAtDesc(latestMember);

        return allMeasurements.stream()
                .collect(Collectors.toMap(
                        m -> m.getMeasuredAt().getYear() + "-" + m.getMeasuredAt().getMonthValue(),
                        m -> MonthlyReportResponse.MonthlyReportListResponse.builder()
                                .monthlyId(m.getMonthlyId())
                                .reportYear(m.getMeasuredAt().getYear())
                                .reportMonth(m.getMeasuredAt().getMonthValue())
                                .measuredAt(m.getMeasuredAt())
                                .build(),
                        (existing, replacement) -> existing, // Desc 정렬 리스트이므로 처음 적재된 기존(최신)본 유지
                        java.util.LinkedHashMap::new
                ))
                .values()
                .stream()
                .collect(Collectors.toList());
    }
    // 30일 경과한 회원 선별-> 알림생성, 저장
    @Override
    @Transactional
    public void createMonthlyMeasurementAlerts() {
        // 오늘 날짜 기준 30일 전 계산
        LocalDateTime targetDate = LocalDateTime.now().minusDays(30);

        LocalDateTime startOfToday = LocalDate.now().atStartOfDay();

        // 마지막 측정일 기준 30일이 경과한 타겟 회원 추출
        List<Member> targetMembers = measurementRepository.findMembersToNotify(targetDate);

        for (Member member : targetMembers) {
            boolean alreadyNotifiedToday = notificationRepository.existsByMemberAndTypeAndSentAtAfter(
                    member,
                    com.project.turtlely.domain.notification.enums.NotificationType.MONTHLY,
                    startOfToday
            );

            if (alreadyNotifiedToday) {
                continue;
            }

            com.project.turtlely.domain.notification.entity.Notification notification =
                    com.project.turtlely.domain.notification.entity.Notification.builder()
                            .member(member)
                            .type(com.project.turtlely.domain.notification.enums.NotificationType.MONTHLY)
                            .content("이번 달 월간 거북목 측정을 할 시기가 되었어요!")
                            .status(com.project.turtlely.domain.notification.enums.NotificationStatus.SENT)
                            .sentAt(LocalDateTime.now())
                            .build();

            notificationRepository.save(notification);
        }
        notificationRepository.flush();
    }
}