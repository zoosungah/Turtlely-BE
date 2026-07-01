package com.project.turtlely.domain.measurement.repository;

import com.project.turtlely.domain.measurement.entity.MonthlyMeasurement;
import com.project.turtlely.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

}