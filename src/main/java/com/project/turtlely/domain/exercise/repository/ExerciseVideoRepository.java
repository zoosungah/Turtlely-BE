package com.project.turtlely.domain.exercise.repository;

import com.project.turtlely.domain.exercise.entity.ExerciseVideo;
import com.project.turtlely.domain.exercise.enums.ExerciseCategory;
import com.project.turtlely.domain.exercise.enums.PostureType;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExerciseVideoRepository extends JpaRepository<ExerciseVideo, Long> {
    // 이미 저장된 영상인지 중복 체크
    boolean existsByYoutubeVideoKey(String youtubeVideoKey);

    // 동적 필터링/검색용 JPQL 쿼리
    @Query("SELECT v FROM ExerciseVideo v " +
            "WHERE (:postureType IS NULL OR :postureType = 'ALL' OR v.postureType = :postureType) " +
            "AND (:category IS NULL OR :category = 'ALL' OR v.exerciseCategory = :category) " +
            "AND (:durationMinutes IS NULL OR v.durationMinutes <= :durationMinutes) " +
            "AND (:keyword IS NULL OR LOWER(v.title) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<ExerciseVideo> searchExerciseVideos(
            @Param("postureType") PostureType postureType,
            @Param("category") ExerciseCategory category,
            @Param("durationMinutes") Integer durationMinutes,
            @Param("keyword") String keyword
    );
}
