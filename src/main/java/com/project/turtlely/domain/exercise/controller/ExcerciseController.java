package com.project.turtlely.domain.exercise.controller;

import com.project.turtlely.domain.exercise.dto.ExerciseResponseDTO;
import com.project.turtlely.domain.exercise.enums.ExerciseCategory;
import com.project.turtlely.domain.exercise.enums.PostureType;
import com.project.turtlely.domain.exercise.exception.code.ExerciseSuccessCode;
import com.project.turtlely.domain.exercise.service.ExerciseService;
import com.project.turtlely.domain.member.service.PrincipalDetails;
import com.project.turtlely.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "운동존 API", description = "운동 영상 조회 및 북마크 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/exercise")
public class ExcerciseController {
    private final ExerciseService exerciseService;

    @Operation(
            summary = "운동존 영상 목록 및 필터링 조회 API by 주성아(개발 완료)",
            description =
                    "- 메인 운동존 화면 및 필터 선택 시 사용되는 영상 목록 조회 API입니다.\n" +
                            "- **모든 필터는 선택 사항(Optional)**이며, 전달하지 않을 경우 전체 영상 목록이 무작위 순서로 반환됩니다.\n" +
                            "### 필터링 조건 \n" +
                            "1. **postureType**: 자세 유형 (`TURTLE_NECK`, `STRAIGHT_NECK`, `REVERSE_C`, `ALL`)\n" +
                            "2. **category**: 운동 종류 (`STRETCHING`, `PHYSICAL_THERAPY`, `FITNESS`, `ETC`, `ALL`)\n" +
                            "3. **durationMinutes**: 최대 영상 시간 (예: `15` 선택 시 15분 이하 영상만 필터링)\n" +
                            "4. **keyword**: 제목 검색어 (대소문자 구분 없음, 부분 일치 검색)\n\n"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "EXERCISE200_1",
                    description = "운동존 목록 조회 성공"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "COMMON401",
                    description = "인증 실패 (JWT 토큰 누락 또는 만료)",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            )
    })
    @GetMapping
    public ApiResponse<ExerciseResponseDTO.ExerciseVideoListDto> getExerciseVideos(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @RequestParam(required = false) PostureType postureType,
            @RequestParam(required = false) ExerciseCategory category,
            @RequestParam(required = false) Integer durationMinutes,
            @RequestParam(required = false) String keyword
    ) {
        Long memberId = principalDetails.getMember().getMemberId();

        ExerciseResponseDTO.ExerciseVideoListDto result = exerciseService.getExerciseVideos(
                memberId, postureType, category, durationMinutes, keyword
        );

        return ApiResponse.onSuccess(ExerciseSuccessCode.EXERCISE_LIST_GET_SUCCESS, result);
    }

    @Operation(
            summary = "영상 북마크 API by 김승연(개발 완료)",
            description = "운동 영상을 북마크합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "북마크 상태 변경 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    value = "{\n" +
                                            "  \"isSuccess\": true,\n" +
                                            "  \"code\": \"BOOKMARK_SUCCESS\",\n" +
                                            "  \"message\": \"북마크 상태가 변경되었습니다.\",\n" +
                                            "  \"result\": {\n" +
                                            "    \"is_bookmarked\": true\n" +
                                            "  }\n" +
                                            "}"
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "요청 ID에 해당하는 운동 영상 존재X",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            )
    })
    @PostMapping("/{video_id}")
    public ApiResponse<ExerciseResponseDTO.ExerciseBookmarkDto> toggleBookmark(
            @PathVariable("video_id") Long videoId,
            @AuthenticationPrincipal PrincipalDetails principalDetails) {

        Long memberId = principalDetails.getMember().getMemberId();
        ExerciseResponseDTO.ExerciseBookmarkDto result = exerciseService.toggleBookmark(videoId, memberId);

        return ApiResponse.onSuccess(ExerciseSuccessCode.BOOKMARK_SUCCESS, result);
    }

    @Operation(
            summary = "내가 북마크한 영상 목록 조회 API by 김승연(개발 완료)",
            description = "사용자가 북마크한 운동 영상 목록을 조회합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "BOOKMARK_LIST_200",
                    description = "내가 북마크한 영상 목록 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    value = "{\n" +
                                            "  \"isSuccess\": true,\n" +
                                            "  \"code\": \"BOOKMARK_LIST_200\",\n" +
                                            "  \"message\": \"내가 북마크한 영상 목록 조회가 완료되었습니다.\",\n" +
                                            "  \"result\": {\n" +
                                            "    \"bookmark_list\": [\n" +
                                            "      {\n" +
                                            "        \"video_id\": 101,\n" +
                                            "        \"title\": \"일자목 교정 스트레칭\",\n" +
                                            "        \"youtube_video_key\": \"vX2c7XbZ\",\n" +
                                            "        \"thumbnail_url\": \"https://...\",\n" +
                                            "        \"duration_minutes\": 5,\n" +
                                            "        \"bookmarked_at\": \"2026-07-28\"\n" +
                                            "      }\n" +
                                            "    ]\n" +
                                            "  }\n" +
                                            "}"
                            )
                    )
            )
    })
    @GetMapping("/bookmarks")
    public ApiResponse<ExerciseResponseDTO.BookmarkListResponseDto> getBookmarkedVideos(
            @AuthenticationPrincipal PrincipalDetails principalDetails) {

        Long memberId = principalDetails.getMember().getMemberId();
        ExerciseResponseDTO.BookmarkListResponseDto result = exerciseService.getBookmarkedVideos(memberId);

        return ApiResponse.onSuccess(ExerciseSuccessCode.BOOKMARK_LIST_GET_SUCCESS, result);
    }

    @Operation(
            summary = "영상 시청 기록 저장 API by 김승연(개발 완료)",
            description = "사용자가 운동 영상을 시청할 때 시청 이력을 저장합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "EX_HISTORY_200",
                    description = "영상 시청 기록 저장 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    value = "{\n" +
                                            "  \"isSuccess\": true,\n" +
                                            "  \"code\": \"EX_HISTORY_200\",\n" +
                                            "  \"message\": \"영상 시청 기록이 성공적으로 저장되었습니다.\",\n" +
                                            "  \"result\": {\n" +
                                            "    \"history_id\": 1,\n" +
                                            "    \"video_id\": 101,\n" +
                                            "    \"watched_at\": \"2026-08-28T21:45:00\"\n" +
                                            "  }\n" +
                                            "}"
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "요청 ID에 해당하는 운동 영상 존재X",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "COMMON401",
                    description = "인증 실패 (JWT 토큰 누락 또는 만료)",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            )
    })
    @PostMapping("/{video_id}/history")
    public ApiResponse<ExerciseResponseDTO.VideoLogResponseDto> recordVideoWatch(
            @PathVariable("video_id") Long videoId,
            @AuthenticationPrincipal PrincipalDetails principalDetails) {

        Long memberId = principalDetails.getMember().getMemberId();
        ExerciseResponseDTO.VideoLogResponseDto result = exerciseService.recordVideoWatch(videoId, memberId);

        return ApiResponse.onSuccess(ExerciseSuccessCode.VIDEO_WATCH_LOG_SUCCESS, result);
    }

    @Operation(
            summary = "운동 가이드 월별 이용 통계 및 맞춤 영상 조회 API by 김승연(개발 완료)",
            description = "특정 연도와 월의 운동 가이드 이용 통계, 최다 시청 영상, 맞춤 추천 영상을 조회합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "EX_STATS_200",
                    description = "운동 가이드 월별 이용 통계 및 추천 영상 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    value = "{\n" +
                                            "  \"isSuccess\": true,\n" +
                                            "  \"code\": \"EX_STATS_200\",\n" +
                                            "  \"message\": \"운동 가이드 월별 이용 통계 및 추천 영상 조회가 완료되었습니다.\",\n" +
                                            "  \"result\": {\n" +
                                            "    \"usage_summary\": {\n" +
                                            "      \"total_watch_count\": 18,\n" +
                                            "      \"watched_video_count\": 7,\n" +
                                            "      \"saved_video_count\": 4\n" +
                                            "    },\n" +
                                            "    \"most_watched_video\": {\n" +
                                            "      \"video_id\": 101,\n" +
                                            "      \"title\": \"목, 어깨 통증이 사라지는 10분 스트레칭! ...\",\n" +
                                            "      \"youtube_video_key\": \"vX2c7XbZ\",\n" +
                                            "      \"thumbnail_url\": \"https://img.youtube.com/vi/vX2c7XbZ/hqdefault.jpg\",\n" +
                                            "      \"category\": \"목/어깨\",\n" +
                                            "      \"watch_count\": 5,\n" +
                                            "      \"is_bookmarked\": true\n" +
                                            "    },\n" +
                                            "    \"recommendations\": {\n" +
                                            "      \"similar_videos\": [\n" +
                                            "        {\n" +
                                            "          \"video_id\": 102,\n" +
                                            "          \"title\": \"목, 어깨 통증이 사라지는 10분 스트레칭\",\n" +
                                            "          \"youtube_video_key\": \"aB3c4DeF\",\n" +
                                            "          \"thumbnail_url\": \"https://img.youtube.com/vi/aB3c4DeF/hqdefault.jpg\",\n" +
                                            "          \"category\": \"목/어깨\",\n" +
                                            "          \"duration_minutes\": 10,\n" +
                                            "          \"is_bookmarked\": false\n" +
                                            "        }\n" +
                                            "      ],\n" +
                                            "      \"new_videos\": [\n" +
                                            "        {\n" +
                                            "          \"video_id\": 104,\n" +
                                            "          \"title\": \"목, 어깨 통증이 사라지는 10분 스트레칭\",\n" +
                                            "          \"youtube_video_key\": \"mN7o8PqR\",\n" +
                                            "          \"thumbnail_url\": \"https://img.youtube.com/vi/mN7o8PqR/hqdefault.jpg\",\n" +
                                            "          \"category\": \"목/어깨\",\n" +
                                            "          \"duration_minutes\": 10,\n" +
                                            "          \"is_bookmarked\": false\n" +
                                            "        }\n" +
                                            "      ]\n" +
                                            "    }\n" +
                                            "  }\n" +
                                            "}"
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "잘못된 연도 또는 월 파라미터 값",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "COMMON401",
                    description = "인증 실패 (JWT 토큰 누락 또는 만료)",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            )
    })
    @GetMapping("/history/monthly")
    public ApiResponse<ExerciseResponseDTO.MonthlyStatsResponseDto> getMonthlyExerciseStats(
            @RequestParam int year,
            @RequestParam int month,
            @AuthenticationPrincipal PrincipalDetails principalDetails) {

        Long memberId = principalDetails.getMember().getMemberId();
        ExerciseResponseDTO.MonthlyStatsResponseDto result = exerciseService.getMonthlyExerciseStats(memberId, year, month);

        return ApiResponse.onSuccess(ExerciseSuccessCode.EXERCISE_STATS_SUCCESS, result);
    }
}