package com.project.turtlely.domain.notification.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.turtlely.domain.notification.enums.NotificationType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

public class NotificationResponse {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "알림 목록 응답 DTO")
    public static class NotificationListDto {
        @JsonProperty("notification_list")
        @Schema(description = "알림 리스트")
        private List<NotificationDto> notificationList;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "개별 알림 상세 DTO")
    public static class NotificationDto {

        @JsonProperty("notification_id")
        @Schema(description = "알림 ID", example = "501")
        private Long notificationId;

        @Schema(description = "알림 유형", example = "MONTHLY")
        private NotificationType type;

        @Schema(description = "알림 내용", example = "N월 월간 리포트가 완성되었습니다!")
        private String content;

        @JsonProperty("is_read")
        @Schema(description = "읽음 여부 (false: 안읽음, true: 읽음)", example = "false")
        private boolean isRead;

        @JsonProperty("created_at")
        @Schema(description = "생성 일시", example = "2026-04-13T10:00:00")
        private LocalDateTime createdAt;

        @JsonProperty("is_read")
        public boolean isRead() {
            return isRead;
        }
    }
}