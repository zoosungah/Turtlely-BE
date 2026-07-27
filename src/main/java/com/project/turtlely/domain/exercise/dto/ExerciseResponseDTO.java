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

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BookmarkListResponseDto {
        @JsonProperty("bookmark_list")
        private List<BookmarkVideoDto> bookmarkList;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BookmarkVideoDto {
        @JsonProperty("video_id")
        private Long videoId;

        private String title;

        @JsonProperty("youtube_video_key")
        private String youtubeVideoKey;

        @JsonProperty("thumbnail_url")
        private String thumbnailUrl;

        @JsonProperty("duration_minutes")
        private Integer durationMinutes;

        public static BookmarkVideoDto from(ExerciseVideo video) {
            return BookmarkVideoDto.builder()
                    .videoId(video.getVideoId())
                    .title(video.getTitle())
                    .youtubeVideoKey(video.getYoutubeVideoKey())
                    .thumbnailUrl(video.getThumbnailUrl())
                    .durationMinutes(video.getDurationMinutes())
                    .build();
        }
    }
}