package com.project.turtlely.domain.daily.repository;

import com.project.turtlely.domain.daily.entity.DailyReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DailyReportRepository extends JpaRepository<DailyReport, Long> {

    List<DailyReport> findByMemberIdOrderByReportDateAsc(Long memberId);

    @Query("SELECT d FROM DailyReport d WHERE d.memberId = :memberId AND d.reportDate BETWEEN :startDate AND :endDate")
    List<DailyReport> findByMemberIdAndReportDateBetween(
            @Param("memberId") Long memberId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    boolean existsByMemberIdAndReportDate(Long memberId, LocalDate reportDate);
}