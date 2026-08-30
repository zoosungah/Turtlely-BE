package com.project.turtlely.domain.exercise.repository;

import com.project.turtlely.domain.exercise.entity.ExerciseVideo;
import com.project.turtlely.domain.exercise.enums.ExerciseCategory;
import com.project.turtlely.domain.exercise.enums.PostureType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    // 동일한 거북목 유형 및 운동 카테고리 영상 중 최다 시청 영상을 제외하고 랜덤 조회
    @Query(value = "SELECT * FROM exercise_video " +
            "WHERE posture_type = :postureType " +
            "AND exercise_category = :category " +
            "AND video_id != :excludeVideoId " +
            "ORDER BY RAND() LIMIT :limit", nativeQuery = true)
    List<ExerciseVideo> findRandomSimilarVideos(
            @Param("postureType") String postureType,
            @Param("category") String category,
            @Param("excludeVideoId") Long excludeVideoId,
            @Param("limit") int limit
    );

    // 다른 운동 카테고리 영상 중 랜덤 조회
    @Query(value = "SELECT * FROM exercise_video " +
            "WHERE exercise_category != :category " +
            "ORDER BY RAND() LIMIT :limit", nativeQuery = true)
    List<ExerciseVideo> findRandomNewVideos(
            @Param("category") String category,
            @Param("limit") int limit
    );

    // 시청 기록이 없을 때 전체 영상 중 랜덤 조회
    @Query(value = "SELECT * FROM exercise_video ORDER BY RAND() LIMIT :limit", nativeQuery = true)
    List<ExerciseVideo> findRandomVideos(@Param("limit") int limit);
}