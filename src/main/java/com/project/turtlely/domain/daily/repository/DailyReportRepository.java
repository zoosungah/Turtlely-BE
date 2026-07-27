package com.project.turtlely.domain.daily.repository;

import com.project.turtlely.domain.daily.entity.DailyReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DailyReportRepository extends JpaRepository<DailyReport, Long> {
    List<DailyReport> findByMemberIdOrderByReportDateAsc(Long memberId);

    // 일일 리포트 측정 기록이 존재하는지 확인하는 메서드 추가
    boolean existsByMemberIdAndReportDate(Long memberId, LocalDate reportDate);
}