package com.project.turtlely.domain.exercise.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "video_log")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class VideoLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "video_log_id")
    private Long videoLogId;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(name = "video_id", nullable = false)
    private Long videoId;

    @Column(name = "watch_time", nullable = false)
    private int watchTime;

    @CreationTimestamp
    @Column(name = "watched_at", nullable = false, updatable = false)
    private LocalDateTime watchedAt;
}