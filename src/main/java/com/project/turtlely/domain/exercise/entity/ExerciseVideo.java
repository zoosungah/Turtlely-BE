package com.project.turtlely.domain.exercise.entity;

import com.project.turtlely.domain.exercise.enums.ExerciseCategory;
import com.project.turtlely.domain.exercise.enums.PostureType;
import com.project.turtlely.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;

@Entity
@Getter
@Builder
public class ExerciseVideo extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long videoId;

    private String youtubeVideoKey; // iframe src용
    private String title;

    @Enumerated(EnumType.STRING)
    private PostureType postureType; // 거북목, 일자목, 역C자목, 정상

    private String thumbnailUrl;

    @Enumerated(EnumType.STRING)
    private ExerciseCategory exerciseCategory; // 스트레칭, 물리치료, 헬스, 기타

    private int durationMinutes;
}
