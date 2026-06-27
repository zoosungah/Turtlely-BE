package com.project.turtlely.domain.daily.repository;

import com.project.turtlely.domain.daily.entity.DailyReport;
import com.project.turtlely.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DailyReportRepository extends JpaRepository<DailyReport, Long> {
    List<DailyReport> findByMemberIdOrderByReportDateAsc(Long memberId);
}