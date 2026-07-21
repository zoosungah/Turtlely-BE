package com.project.turtlely.domain.exercise.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.turtlely.domain.exercise.entity.ExerciseVideo;
import com.project.turtlely.domain.exercise.enums.ExerciseCategory;
import com.project.turtlely.domain.exercise.enums.PostureType;
import com.project.turtlely.domain.exercise.repository.ExerciseVideoRepository;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Service
@RequiredArgsConstructor
public class YoutubeFetchService {
    private final WebClient youtubeWebClient;
    private final ExerciseVideoRepository exerciseVideoRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${youtube.api.key}")
    private String apiKey;

    /**
     * 지정한 검색어와 개수만큼 유튜브에서 최신 영상을 수집하여 DB에 저장
     */
    @Transactional
    public int fetchAndSaveVideos(String query, int maxResults, PostureType postureType, ExerciseCategory category) {
        try {
            // 1. String 타입으로 안전하게 JSON 응답 수신
            String responseBody = youtubeWebClient.get()
                    .uri(uriBuilder -> uriBuilder.path("/search")
                            .queryParam("key", apiKey)
                            .queryParam("part", "snippet")
                            .queryParam("q", query)
                            .queryParam("type", "video")
                            .queryParam("maxResults", maxResults)
                            .queryParam("order", "relevance")
                            .build())
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            if (responseBody == null) {
                return 0;
            }

            // 2. ObjectMapper를 사용해 JsonNode로 변환
            JsonNode response = objectMapper.readTree(responseBody);

            if (!response.has("items")) {
                log.warn("유튜브 API 응답에 items가 없습니다: {}", responseBody);
                return 0;
            }

            JsonNode items = response.get("items");
            int savedCount = 0;

            for (JsonNode item : items) {
                // videoId 파싱 전 유효성 검사
                if (!item.has("id") || !item.get("id").has("videoId")) {
                    continue;
                }

                String videoKey = item.get("id").get("videoId").asText();

                if (exerciseVideoRepository.existsByYoutubeVideoKey(videoKey)) {
                    continue;
                }

                String title = item.get("snippet").get("title").asText();
                String thumbnailUrl = item.get("snippet").get("thumbnails").get("high").get("url").asText();
                int durationMinutes = new Random().nextInt(31); // 0~30분

                ExerciseVideo video = ExerciseVideo.builder()
                        .youtubeVideoKey(videoKey)
                        .title(title)
                        .postureType(postureType)
                        .exerciseCategory(category)
                        .thumbnailUrl(thumbnailUrl)
                        .durationMinutes(durationMinutes)
                        .build();

                exerciseVideoRepository.save(video);
                savedCount++;
            }

            log.info("유튜브 영상 수집 성공 - 키워드: {}, 저장 수: {}", query, savedCount);
            return savedCount;

        } catch (Exception e) {
            log.error("유튜브 영상 수집 실패: {}", e.getMessage(), e);
            return 0;
        }
    }
}