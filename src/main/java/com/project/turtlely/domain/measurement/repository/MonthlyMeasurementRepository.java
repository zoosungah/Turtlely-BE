package com.project.turtlely.domain.measurement.repository;

import com.project.turtlely.domain.measurement.entity.MonthlyMeasurement;
import com.project.turtlely.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MonthlyMeasurementRepository extends JpaRepository<MonthlyMeasurement, Long> {
    Optional<MonthlyMeasurement> findByMonthlyIdAndMember(Long monthlyId, Member member);

    @Query("SELECT m FROM MonthlyMeasurement m WHERE m.member = :member ORDER BY m.measuredAt DESC")
    List<MonthlyMeasurement> findTop6ByMemberOrderByMeasuredAtDesc(@Param("member") Member member);

    //  연/월 조건 필터링
    @Query("SELECT m FROM MonthlyMeasurement m WHERE m.member = :member " +
            "AND FUNCTION('YEAR', m.measuredAt) = :year " +
            "AND FUNCTION('MONTH', m.measuredAt) = :month " +
            "ORDER BY m.measuredAt DESC")
    List<MonthlyMeasurement> findByMemberAndYearAndMonthCustom(@Param("member") Member member, @Param("year") int year, @Param("month") int month);

    @Query("SELECT m FROM MonthlyMeasurement m WHERE m.member = :member ORDER BY m.measuredAt DESC")
    List<MonthlyMeasurement> findTopByMemberOrderByMeasuredAtDescCustom(@Param("member") Member member);

    List<MonthlyMeasurement> findByMemberOrderByMeasuredAtDesc(Member member);

    // 가장 최근 측정일이 30일 이상 지난 회원 목록과 마지막 측정 데이터를 조회
    @Query("SELECT m.member FROM MonthlyMeasurement m " +
            "WHERE m.measuredAt = (SELECT MAX(sub.measuredAt) FROM MonthlyMeasurement sub WHERE sub.member = m.member) " +
            "AND m.measuredAt <= :targetDate")
    List<Member> findMembersToNotify(@Param("targetDate") java.time.LocalDateTime targetDate);

    // 오늘 측정 기록이 있는지 체크
    boolean existsByMemberAndMeasuredAtBetween(Member member, LocalDateTime start, LocalDateTime end);
}