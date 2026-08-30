package com.project.turtlely.domain.exercise.service;
import com.project.turtlely.domain.exercise.dto.ExerciseResponseDTO;
import com.project.turtlely.domain.exercise.entity.ExerciseVideo;
import com.project.turtlely.domain.exercise.entity.VideoBookmark;
import com.project.turtlely.domain.exercise.entity.VideoLog;
import com.project.turtlely.domain.exercise.enums.ExerciseCategory;
import com.project.turtlely.domain.exercise.enums.PostureType;
import com.project.turtlely.domain.exercise.exception.ExcerciseException;
import com.project.turtlely.domain.exercise.exception.code.ExerciseErrorCode;
import com.project.turtlely.domain.exercise.repository.ExerciseVideoRepository;
import com.project.turtlely.domain.exercise.repository.VideoBookmarkRepository;
import com.project.turtlely.domain.exercise.repository.VideoLogRepository;
import com.project.turtlely.domain.member.entity.Member;
import com.project.turtlely.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ExerciseService {
    private final ExerciseVideoRepository exerciseVideoRepository;
    private final MemberRepository memberRepository;
    private final VideoBookmarkRepository videoBookmarkRepository;
    private final VideoLogRepository videoLogRepository;

    public ExerciseResponseDTO.ExerciseVideoListDto getExerciseVideos(
            Long memberId,
            PostureType postureType,
            ExerciseCategory category,
            Integer durationMinutes,
            String keyword
    ) {
        // 1. 로그인 유저 조회
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        // 2. 동적 쿼리로 DB에서 조건에 맞는 영상 목록 조회
        List<ExerciseVideo> videos = exerciseVideoRepository.searchExerciseVideos(
                postureType, category, durationMinutes, keyword
        );

        // 3. 필터 선택 없는 기본 메인 진입 시 '하루 단위'로 동일한 무작위 순서 유지
        if (postureType == null && category == null && durationMinutes == null && keyword == null) {
            // 오늘 날짜(예: 20260728)를 숫자 Seed 값으로 사용
            long dailySeed = LocalDate.now().toEpochDay();
            Random dailyRandom = new Random(dailySeed);

            // 날짜 기반 Random 객체를 이용하여 셔플 (하루 동안은 순서 고정)
            Collections.shuffle(videos, dailyRandom);
        }

        // 4. 북마크 여부 체크 및 DTO 변환
        List<ExerciseResponseDTO.ExerciseVideoDto> videoDtos = videos.stream()
                .map(video -> {
                    // ✅ 북마크 테이블이 있다면 아래처럼 확인 (임시는 false 처리)
                    // boolean isBookmarked = exerciseBookmarkRepository.existsByMemberAndExerciseVideo(member, video);
                    boolean isBookmarked = videoBookmarkRepository.existsByMemberAndExerciseVideo(member, video);
                    return ExerciseResponseDTO.ExerciseVideoDto.of(video, isBookmarked);
                })
                .toList();

        return ExerciseResponseDTO.ExerciseVideoListDto.builder()
                .nickname(member.getNickname())
                .videoList(videoDtos)
                .build();
    }

    // 영상 북마크
    @Transactional
    public ExerciseResponseDTO.ExerciseBookmarkDto toggleBookmark(Long videoId, Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        ExerciseVideo video = exerciseVideoRepository.findById(videoId)
                .orElseThrow(() -> new ExcerciseException(ExerciseErrorCode.VIDEO_NOT_FOUND));

        Optional<VideoBookmark> existingBookmark = videoBookmarkRepository.findByMemberAndExerciseVideo(member, video);

        boolean isBookmarked;

        if (existingBookmark.isPresent()) {
            videoBookmarkRepository.delete(existingBookmark.get());
            isBookmarked = false;
        } else {
            VideoBookmark bookmark = VideoBookmark.builder()
                    .member(member)
                    .exerciseVideo(video)
                    .build();
            videoBookmarkRepository.save(bookmark);
            isBookmarked = true;
        }

        return ExerciseResponseDTO.ExerciseBookmarkDto.builder()
                .isBookmarked(isBookmarked)
                .build();
    }

    // 내가 북마크한 영상 목록 조회
    public ExerciseResponseDTO.BookmarkListResponseDto getBookmarkedVideos(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        List<VideoBookmark> bookmarks = videoBookmarkRepository.findAllByMember(member);

        List<ExerciseResponseDTO.BookmarkVideoDto> bookmarkVideoDtos = bookmarks.stream()
                .map(bookmark -> ExerciseResponseDTO.BookmarkVideoDto.from(bookmark))
                .toList();

        return ExerciseResponseDTO.BookmarkListResponseDto.builder()
                .bookmarkList(bookmarkVideoDtos)
                .build();
    }

    // 영상 시청 기록 저장
    @Transactional
    public ExerciseResponseDTO.VideoLogResponseDto recordVideoWatch(Long videoId, Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        ExerciseVideo video = exerciseVideoRepository.findById(videoId)
                .orElseThrow(() -> new ExcerciseException(ExerciseErrorCode.VIDEO_NOT_FOUND));

        VideoLog videoLog = VideoLog.builder()
                .memberId(member.getMemberId())
                .videoId(video.getVideoId())
                .watchTime(video.getDurationMinutes())
                .build();

        VideoLog savedLog = videoLogRepository.save(videoLog);

        return ExerciseResponseDTO.VideoLogResponseDto.from(savedLog);
    }

    // 운동 가이드 월별 이용 통계 및 맞춤 영상 조회
    public ExerciseResponseDTO.MonthlyStatsResponseDto getMonthlyExerciseStats(Long memberId, int year, int month) {
        // 연도 및 월 파라미터 유효성 검증
        if (year < 2000 || month < 1 || month > 12) {
            throw new ExcerciseException(ExerciseErrorCode.EX_PARAM_ERROR);
        }

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        // 1. 해당 월의 시작일과 종료일 계산
        LocalDate startLocalDate = LocalDate.of(year, month, 1);
        LocalDate endLocalDate = startLocalDate.withDayOfMonth(startLocalDate.lengthOfMonth());
        LocalDateTime startOfMonth = startLocalDate.atStartOfDay();
        LocalDateTime endOfMonth = endLocalDate.atTime(LocalTime.MAX);

        // 2. 이용 통계 집계
        int totalWatchCount = videoLogRepository.countTotalWatchesByMonth(memberId, startOfMonth, endOfMonth);
        int watchedVideoCount = videoLogRepository.countWatchedVideosByMonth(memberId, startOfMonth, endOfMonth);
        int savedVideoCount = videoBookmarkRepository.findAllByMember(member).size();

        ExerciseResponseDTO.UsageSummaryDto usageSummary = ExerciseResponseDTO.UsageSummaryDto.builder()
                .totalWatchCount(totalWatchCount)
                .watchedVideoCount(watchedVideoCount)
                .savedVideoCount(savedVideoCount)
                .build();

        // 3. 당월 최다 시청 영상 조회
        List<Object[]> mostWatchedResults = videoLogRepository.findMostWatchedVideoIdByMonth(memberId, startOfMonth, endOfMonth);

        ExerciseResponseDTO.MostWatchedVideoDto mostWatchedVideoDto = null;
        List<ExerciseVideo> similarVideos;
        List<ExerciseVideo> newVideos;

        if (!mostWatchedResults.isEmpty()) {
            Object[] topResult = mostWatchedResults.get(0);
            Long mostWatchedVideoId = (Long) topResult[0];
            int mostWatchedCount = ((Number) topResult[1]).intValue();

            ExerciseVideo topVideo = exerciseVideoRepository.findById(mostWatchedVideoId).orElse(null);

            if (topVideo != null) {
                boolean isTopBookmarked = videoBookmarkRepository.existsByMemberAndExerciseVideo(member, topVideo);
                mostWatchedVideoDto = ExerciseResponseDTO.MostWatchedVideoDto.builder()
                        .videoId(topVideo.getVideoId())
                        .title(topVideo.getTitle())
                        .youtubeVideoKey(topVideo.getYoutubeVideoKey())
                        .thumbnailUrl(topVideo.getThumbnailUrl())
                        .category(topVideo.getPostureType() != null ? topVideo.getPostureType().name() : "기타")
                        .watchCount(mostWatchedCount)
                        .isBookmarked(isTopBookmarked)
                        .build();

                // 비슷한 영상
                similarVideos = exerciseVideoRepository.findRandomSimilarVideos(
                        topVideo.getPostureType().name(),
                        topVideo.getExerciseCategory().name(),
                        topVideo.getVideoId(),
                        2
                );

                // 새로운 운동 영상
                newVideos = exerciseVideoRepository.findRandomNewVideos(
                        topVideo.getExerciseCategory().name(),
                        2
                );
            } else {
                similarVideos = exerciseVideoRepository.findRandomVideos(2);
                newVideos = exerciseVideoRepository.findRandomVideos(2);
            }
        } else {
            // 시청 기록이 없을 때: 전체 영상 중 랜덤 2개씩 추출
            similarVideos = exerciseVideoRepository.findRandomVideos(2);
            newVideos = exerciseVideoRepository.findRandomVideos(2);
        }

        // 4. 북마크 여부 매핑
        List<ExerciseResponseDTO.RecommendedVideoDto> similarVideoDtos = similarVideos.stream()
                .map(v -> ExerciseResponseDTO.RecommendedVideoDto.of(v, videoBookmarkRepository.existsByMemberAndExerciseVideo(member, v)))
                .toList();

        List<ExerciseResponseDTO.RecommendedVideoDto> newVideoDtos = newVideos.stream()
                .map(v -> ExerciseResponseDTO.RecommendedVideoDto.of(v, videoBookmarkRepository.existsByMemberAndExerciseVideo(member, v)))
                .toList();

        ExerciseResponseDTO.RecommendationDto recommendations = ExerciseResponseDTO.RecommendationDto.builder()
                .similarVideos(similarVideoDtos)
                .newVideos(newVideoDtos)
                .build();

        return ExerciseResponseDTO.MonthlyStatsResponseDto.builder()
                .usageSummary(usageSummary)
                .mostWatchedVideo(mostWatchedVideoDto)
                .recommendations(recommendations)
                .build();
    }
}