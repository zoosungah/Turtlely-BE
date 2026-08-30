package com.project.turtlely.domain.exercise.repository;

import com.project.turtlely.domain.exercise.entity.VideoLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface VideoLogRepository extends JpaRepository<VideoLog, Long> {

    // 최근 3개월 동안 시청한 영상 로그들을 조회하여 총 시청 시간을 계산
    @Query("SELECT v FROM VideoLog v WHERE v.memberId = :memberId AND v.watchedAt >= :threeMonthsAgo")
    List<VideoLog> findRecentWatchLogs(@Param("memberId") Long memberId, @Param("threeMonthsAgo") LocalDateTime threeMonthsAgo);

    // 1. 특정 기간 동안의 총 시청 횟수
    @Query("SELECT COUNT(v) FROM VideoLog v WHERE v.memberId = :memberId AND v.watchedAt >= :startOfMonth AND v.watchedAt <= :endOfMonth")
    int countTotalWatchesByMonth(@Param("memberId") Long memberId, @Param("startOfMonth") LocalDateTime startOfMonth, @Param("endOfMonth") LocalDateTime endOfMonth);

    // 2. 특정 기간 동안 시청한 중복 제거 영상 개수
    @Query("SELECT COUNT(DISTINCT v.videoId) FROM VideoLog v WHERE v.memberId = :memberId AND v.watchedAt >= :startOfMonth AND v.watchedAt <= :endOfMonth")
    int countWatchedVideosByMonth(@Param("memberId") Long memberId, @Param("startOfMonth") LocalDateTime startOfMonth, @Param("endOfMonth") LocalDateTime endOfMonth);

    // 3. 특정 기간 동안 가장 많이 시청한 영상 ID 및 시청 횟수 조회 (최다 시청 1위)
    @Query("SELECT v.videoId, COUNT(v) FROM VideoLog v " +
            "WHERE v.memberId = :memberId AND v.watchedAt >= :startOfMonth AND v.watchedAt <= :endOfMonth " +
            "GROUP BY v.videoId " +
            "ORDER BY COUNT(v) DESC")
    List<Object[]> findMostWatchedVideoIdByMonth(@Param("memberId") Long memberId, @Param("startOfMonth") LocalDateTime startOfMonth, @Param("endOfMonth") LocalDateTime endOfMonth);
}