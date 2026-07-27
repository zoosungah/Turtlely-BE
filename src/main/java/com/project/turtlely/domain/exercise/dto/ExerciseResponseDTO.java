package com.project.turtlely.domain.exercise.dto;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.turtlely.domain.exercise.entity.ExerciseVideo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
public class ExerciseResponseDTO {
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ExerciseVideoListDto {
        private String nickname;
        private List<ExerciseVideoDto> videoList;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ExerciseVideoDto {
        private Long videoId;
        private String youtubeVideoKey;
        private String title;
        private String thumbnailUrl;
        private Integer durationMinutes;
        private Boolean isBookmarked;

        public static ExerciseVideoDto of(ExerciseVideo video, boolean isBookmarked) {
            return ExerciseVideoDto.builder()
                    .videoId(video.getVideoId())
                    .youtubeVideoKey(video.getYoutubeVideoKey())
                    .title(video.getTitle())
                    .thumbnailUrl(video.getThumbnailUrl())
                    .durationMinutes(video.getDurationMinutes())
                    .isBookmarked(isBookmarked)
                    .build();
        }
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ExerciseBookmarkDto {
        @JsonProperty("is_bookmarked")
        private Boolean isBookmarked;
    }
}