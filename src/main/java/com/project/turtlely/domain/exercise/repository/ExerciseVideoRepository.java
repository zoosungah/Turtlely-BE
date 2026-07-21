package com.project.turtlely.domain.exercise.repository;

import com.project.turtlely.domain.exercise.entity.ExerciseVideo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExerciseVideoRepository extends JpaRepository<ExerciseVideo, Long> {
    // 이미 저장된 영상인지 중복 체크
    boolean existsByYoutubeVideoKey(String youtubeVideoKey);
}
