package com.project.turtlely.domain.exercise.controller;

import com.project.turtlely.domain.exercise.exception.code.ExerciseSuccessCode;
import com.project.turtlely.domain.exercise.scheduler.ExerciseScheduler;
import com.project.turtlely.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/exercise")
public class ExerciseAdminController {
    private final ExerciseScheduler exerciseScheduler;

    /**
     * 운동존 영상 세팅 API
     */
    @Operation(
            summary = "운동존 영상 세팅 API(프론트 사용X) by 주성아(개발 완료)",
            description = "운동존 db에 Youtube API v3로 영상 긁어오는 API, 매달 1일에 10개씩 영상 추가됨"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "EXERCISE200_2", description = "초기 운동 영상 수집에 성공하였습니다.")
    })
    @PostMapping("/init-videos")
    public ApiResponse<String> initVideos() {
        exerciseScheduler.initThirtyVideos();
        return ApiResponse.onSuccess(ExerciseSuccessCode.INIT_VIDEOS_SUCCESS, "초기 30개 영상 수집이 완료되었습니다.");
    }
}
