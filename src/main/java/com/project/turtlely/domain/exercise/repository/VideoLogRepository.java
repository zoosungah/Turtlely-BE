package com.project.turtlely.domain.exercise.repository;

import com.project.turtlely.domain.exercise.entity.VideoLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface VideoLogRepository extends JpaRepository<VideoLog, Long> {

    // 최근 3개월간의 시청 기록 조회
    @Query("SELECT v FROM VideoLog v WHERE v.memberId = :memberId AND v.watchedAt >= :threeMonthsAgo")
    List<VideoLog> findRecentWatchLogs(@Param("memberId") Long memberId, @Param("threeMonthsAgo") LocalDateTime threeMonthsAgo);
}