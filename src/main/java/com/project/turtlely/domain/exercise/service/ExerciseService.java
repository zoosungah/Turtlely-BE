package com.project.turtlely.domain.exercise.service;
import com.project.turtlely.domain.exercise.dto.ExerciseResponseDTO;
import com.project.turtlely.domain.exercise.entity.ExerciseVideo;
import com.project.turtlely.domain.exercise.entity.VideoBookmark;
import com.project.turtlely.domain.exercise.enums.ExerciseCategory;
import com.project.turtlely.domain.exercise.enums.PostureType;
import com.project.turtlely.domain.exercise.exception.ExcerciseException;
import com.project.turtlely.domain.exercise.exception.code.ExerciseErrorCode;
import com.project.turtlely.domain.exercise.repository.ExerciseVideoRepository;
import com.project.turtlely.domain.exercise.repository.VideoBookmarkRepository;
import com.project.turtlely.domain.member.entity.Member;
import com.project.turtlely.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
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
}