package com.project.turtlely.domain.exercise.scheduler;

import com.project.turtlely.domain.exercise.enums.ExerciseCategory;
import com.project.turtlely.domain.exercise.service.YoutubeFetchService;
import com.project.turtlely.domain.exercise.enums.PostureType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExerciseScheduler {

    private final YoutubeFetchService youtubeFetchService;
    private final Random random = new Random();

    // 1. 거북목(TURTLE_NECK) 키워드 풀
    private final List<String> turtleKeywords = List.of(
            "거북목 교정 운동", "거북목 스트레칭", "거북목 폼롤러 루틴",
            "거북목 교정 밴드", "거북목 목 어깨 통증", "거북목 교정 10분",
            "라운드숄더 거북목 교정", "거북목 자세 교정", "거북목 교정 밴드 루틴"
    );

    // 2. 일자목(STRAIGHT_NECK) 키워드 풀
    private final List<String> straightKeywords = List.of(
            "일자목 스트레칭", "일자목 교정 운동", "일자목 도수치료",
            "일자목 베개 운동", "목 뻐근할 때 스트레칭", "일자목 재활 운동",
            "일자목 통증 완화", "경추 굴곡 스트레칭", "목 커브 회복 운동"
    );

    // 3. 역C자목/버섯증후군(REVERSE_C) 키워드 풀
    private final List<String> reverseCKeywords = List.of(
            "버섯증후군 교정 운동", "목 뒤 혹 교정", "버팔로 험프 스트레칭",
            "역C자목 교정 루틴", "목 어깨 솟음 교정", "뒷목 뻐근함 스트레칭",
            "버섯목 교정 폼롤러", "경추 7번 교정 운동"
    );

    // 4. 카테고리별(STRETCHING / PHYSICAL_THERAPY / FITNESS / ETC) 키워드 풀
    private final List<String> generalKeywords = List.of(
            "승모근 스트레칭", "상체 스트레칭 루틴", "목 어깨 헬스",
            "체형 교정 스트레칭", "데스크톱 거북목 예방", "수험생 직장인 목 스트레칭",
            "라운드숄더 덤벨 운동", "폼롤러 등 목 스트레칭", "어깨 관절 가동성 운동"
    );

    /**
     * [초기화] 서버 초기 세팅용 - 다양한 키워드로 30개 수집
     */
    public void initThirtyVideos() {
        log.info("=== 다양한 키워드로 초기 30개 유튜브 영상 수집 시작 ===");

        // 각 타입별로 무작위 키워드 선정하여 수집
        youtubeFetchService.fetchAndSaveVideos(getRandomElement(turtleKeywords), 8, PostureType.TURTLE_NECK, ExerciseCategory.STRETCHING);
        youtubeFetchService.fetchAndSaveVideos(getRandomElement(straightKeywords), 8, PostureType.STRAIGHT_NECK, ExerciseCategory.PHYSICAL_THERAPY);
        youtubeFetchService.fetchAndSaveVideos(getRandomElement(reverseCKeywords), 7, PostureType.REVERSE_C, ExerciseCategory.FITNESS);
        youtubeFetchService.fetchAndSaveVideos(getRandomElement(generalKeywords), 7, PostureType.ALL, ExerciseCategory.ETC);
    }

    /**
     * [매월 자동 스케줄러] 매달 1일 새벽 3시에 완전히 새로운 조합으로 10개 수집
     */
    @Scheduled(cron = "0 0 3 1 * ?")
    public void autoFetchMonthlyVideos() {
        log.info("=== 매월 신규 10개 다양한 키워드 유튜브 영상 수집 시작 ===");

        // 매달 무작위 키워드를 다르게 조합해서 10개 수집
        youtubeFetchService.fetchAndSaveVideos(getRandomElement(turtleKeywords), 3, PostureType.TURTLE_NECK, ExerciseCategory.STRETCHING);
        youtubeFetchService.fetchAndSaveVideos(getRandomElement(straightKeywords), 3, PostureType.STRAIGHT_NECK, ExerciseCategory.PHYSICAL_THERAPY);
        youtubeFetchService.fetchAndSaveVideos(getRandomElement(reverseCKeywords), 2, PostureType.REVERSE_C, ExerciseCategory.FITNESS);
        youtubeFetchService.fetchAndSaveVideos(getRandomElement(generalKeywords), 2, PostureType.ALL, ExerciseCategory.ETC);
    }

    // 리스트에서 무작위 요소 추출 메서드
    private String getRandomElement(List<String> list) {
        return list.get(random.nextInt(list.size()));
    }
}
