package com.project.turtlely.domain.exercise.dto;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.turtlely.domain.exercise.entity.ExerciseVideo;
import com.project.turtlely.domain.exercise.entity.VideoBookmark;
import com.project.turtlely.domain.exercise.entity.VideoLog;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
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

        @JsonProperty("is_bookmarked")
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

        @JsonProperty("bookmarked_at")
        private String bookmarkedAt;

        public static BookmarkVideoDto from(VideoBookmark bookmark) {
            ExerciseVideo video = bookmark.getExerciseVideo();
            String formattedDate = bookmark.getCreatedAt() != null
                    ? bookmark.getCreatedAt().toLocalDate().toString()
                    : null;

            return BookmarkVideoDto.builder()
                    .videoId(video.getVideoId())
                    .title(video.getTitle())
                    .youtubeVideoKey(video.getYoutubeVideoKey())
                    .thumbnailUrl(video.getThumbnailUrl())
                    .durationMinutes(video.getDurationMinutes())
                    .bookmarkedAt(formattedDate)
                    .build();
        }

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

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VideoLogResponseDto {
        @JsonProperty("history_id")
        private Long historyId;

        @JsonProperty("video_id")
        private Long videoId;

        @JsonProperty("watched_at")
        private LocalDateTime watchedAt;

        public static VideoLogResponseDto from(VideoLog videoLog) {
            return VideoLogResponseDto.builder()
                    .historyId(videoLog.getVideoLogId())
                    .videoId(videoLog.getVideoId())
                    .watchedAt(videoLog.getWatchedAt())
                    .build();
        }
    }
}