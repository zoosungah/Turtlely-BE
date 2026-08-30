package com.project.turtlely.domain.measurement.service;

import com.project.turtlely.domain.measurement.exception.code.MeasurementErrorCode;
import org.springframework.stereotype.Service;

@Service
public class MonthlyHWService {
    /**
     * 비전 CVA 각도와 하드웨어 3축 Raw 가속도를 받아 보정 상수 C 계산
     */
    public float calculateCalibrationC(double cvAngle, double x, double y, double z) {
        // 1. 하드웨어 벡터 크기 계산
        double vectorMagnitude = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2));

        if (vectorMagnitude == 0) {
            throw new MeasurementErrorCode.MeasurementCustomException(MeasurementErrorCode.INVALID_FRAME_DATA);
        }

        // 2. 아크코사인 공식 기반 Pitch 계산
        double cosVal = Math.max(-1.0, Math.min(1.0, z / vectorMagnitude));
        double hwPitch = Math.toDegrees(Math.acos(cosVal));

        // 3. C 공식(비전 각도 - HW Pitch)
        double computedC = cvAngle - hwPitch;

        // 소수점 둘째자리 반올림 후 float 변환 리턴
        return (float) (Math.round(computedC * 100) / 100.0);
    }
}
