package com.project.turtlely.domain.measurement.service;

import com.project.turtlely.domain.measurement.dto.MonthlyReportResponse;
import com.project.turtlely.domain.measurement.dto.ReportAnalyzeRequest;
import com.project.turtlely.domain.measurement.dto.ReportAnalyzeRequest.FrameData;
import com.project.turtlely.domain.measurement.entity.MonthlyMeasurement;
import com.project.turtlely.domain.measurement.repository.MonthlyMeasurementRepository;
import com.project.turtlely.domain.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Override
    public MonthlyReportResponse getMonthlyReport(Long monthlyId, Member member) {
        MonthlyMeasurement currentMeasurement = measurementRepository.findByMonthlyIdAndMember(monthlyId, member).orElse(null);
        List<MonthlyMeasurement> rawHistory = measurementRepository.findTop6ByMemberOrderByMeasuredAtDesc(member);

        List<MonthlyMeasurement> chronologicalHistory = new ArrayList<>(rawHistory);
        Collections.reverse(chronologicalHistory);

        List<MonthlyReportResponse.HistoryDto> cvaHistory = chronologicalHistory.stream()
                .map(h -> MonthlyReportResponse.HistoryDto.builder()
                        .month(h.getMeasuredAt().getMonthValue() + "월")
                        .angle((double) h.getCvaAngle())
                        .build()).collect(Collectors.toList());

        List<MonthlyReportResponse.HistoryDto> craHistory = chronologicalHistory.stream()
                .map(h -> MonthlyReportResponse.HistoryDto.builder()
                        .month(h.getMeasuredAt().getMonthValue() + "월")
                        .angle((double) h.getCraAngle())
                        .build()).collect(Collectors.toList());

        if (currentMeasurement == null) {
            return MonthlyReportResponse.builder()
                    .dataStatus("NOT_YET").monthlyId(null).nickname(member.getNickname()).postureType("데이터 없음")
                    .score(null).cvaAngle(null).craAngle(null).cvaHistory(cvaHistory).craHistory(craHistory)
                    .alarmSet(false).measuredAt(null).build();
        }

        return MonthlyReportResponse.builder()
                .dataStatus("AVAILABLE").monthlyId(currentMeasurement.getMonthlyId()).nickname(member.getNickname())
                .postureType(currentMeasurement.getPostureType()).score(currentMeasurement.getScore())
                .cvaAngle((double) currentMeasurement.getCvaAngle()).craAngle((double) currentMeasurement.getCraAngle())
                .cvaHistory(cvaHistory).craHistory(craHistory).alarmSet(false).measuredAt(currentMeasurement.getMeasuredAt()).build();
    }

    @Override
    @Transactional
    public MonthlyReportResponse analyzeAndSaveReport(ReportAnalyzeRequest request, Member member) {
        if (request.getFrames() == null || request.getFrames().isEmpty()) {
            throw new IllegalArgumentException("분석할 프레임 데이터가 존재하지 않습니다.");
        }

        FrameData bestFrame = null;
        double minScore = Double.MAX_VALUE;
        FrameData prevFrame = null;

        // 최적 프레임 탐색
        for (int i = 0; i < request.getFrames().size(); i++) {
            FrameData current = request.getFrames().get(i);

            // 예외 필터링
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

            // 움직임 70% + 화면 중앙 정렬도 30% 패널티 연산
            double calculatedScore = (movement * 0.7) + (centerDistance * 0.3);

            if (calculatedScore < minScore) {
                minScore = calculatedScore;
                bestFrame = current;
            }
            prevFrame = current;
        }

        if (bestFrame == null) {
            throw new IllegalStateException("랜드마크를 확실하게 찾을 수 없습니다.");
        }

        // 2. CVA 각도 수치화 공식 연산
        double deltaYCva = Math.abs(bestFrame.getC7Y() - bestFrame.getTragusY());
        double deltaXCva = Math.abs(bestFrame.getC7X() - bestFrame.getTragusX());
        double cvaAngle = Math.toDegrees(Math.atan2(deltaYCva, deltaXCva));

        // 3. CRA 가동 범위 가속 벡터 연산
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
        int finalScore;

        if (cvaAngle >= 50) {
            postureType = "정상";
            finalScore = 100;
        } else if (cvaAngle >= 45) {
            postureType = "일자목";
            finalScore = 80;
        } else if (cvaAngle >= 40) {
            postureType = "거북목";
            finalScore = 60;
        } else {
            postureType = "역C자목";
            finalScore = 40;
        }

        MonthlyMeasurement measurement = MonthlyMeasurement.builder()
                .member(member)
                .cvaAngle((float) cvaAngle)
                .craAngle((float) craAngle)
                .postureType(postureType)
                .score(finalScore)
                .measuredAt(LocalDateTime.now())
                .build();

        MonthlyMeasurement saved = measurementRepository.save(measurement);
        return getMonthlyReport(saved.getMonthlyId(), member);
    }
}