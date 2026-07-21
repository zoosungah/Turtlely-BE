package com.project.turtlely.domain.exercise.service;
import com.project.turtlely.domain.exercise.dto.ExerciseResponseDTO;
import com.project.turtlely.domain.exercise.entity.ExerciseVideo;
import com.project.turtlely.domain.exercise.enums.ExerciseCategory;
import com.project.turtlely.domain.exercise.enums.PostureType;
import com.project.turtlely.domain.exercise.repository.ExerciseVideoRepository;
import com.project.turtlely.domain.member.entity.Member;
import com.project.turtlely.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ExerciseService {
    private final ExerciseVideoRepository exerciseVideoRepository;
    private final MemberRepository memberRepository;

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

        // 3. 필터 선택 없는 기본 메인 진입 시 무작위(랜덤)로 순서 섞어주기
        if (postureType == null && category == null && durationMinutes == null && keyword == null) {
            Collections.shuffle(videos);
        }

        // 4. 북마크 여부 체크 및 DTO 변환
        List<ExerciseResponseDTO.ExerciseVideoDto> videoDtos = videos.stream()
                .map(video -> {
                    // ✅ 북마크 테이블이 있다면 아래처럼 확인 (임시는 false 처리)
                    // boolean isBookmarked = exerciseBookmarkRepository.existsByMemberAndExerciseVideo(member, video);
                    boolean isBookmarked = false;
                    return ExerciseResponseDTO.ExerciseVideoDto.of(video, isBookmarked);
                })
                .toList();

        return ExerciseResponseDTO.ExerciseVideoListDto.builder()
                .nickname(member.getNickname())
                .videoList(videoDtos)
                .build();
    }
}
