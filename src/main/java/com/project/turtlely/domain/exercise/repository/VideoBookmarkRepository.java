package com.project.turtlely.domain.exercise.repository;

import com.project.turtlely.domain.exercise.entity.ExerciseVideo;
import com.project.turtlely.domain.exercise.entity.VideoBookmark;
import com.project.turtlely.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VideoBookmarkRepository extends JpaRepository<VideoBookmark, Long> {

    // 북마크 존재 여부 확인용
    boolean existsByMemberAndExerciseVideo(Member member, ExerciseVideo exerciseVideo);
    Optional<VideoBookmark> findByMemberAndExerciseVideo(Member member, ExerciseVideo exerciseVideo);

    // 북마크 목록 조회
    List<VideoBookmark> findAllByMember(Member member);
}