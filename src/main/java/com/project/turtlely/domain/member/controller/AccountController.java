package com.project.turtlely.domain.member.controller;

import com.project.turtlely.domain.member.exception.code.MemberSuccessCode;
import com.project.turtlely.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "사용자 계정 관리", description = "사용자 계정 관리 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class AccountController {
    /**
     * 로그아웃 API
     */
    @Operation(
            summary = "로그아웃 API",
            description = "서버 측에 저장된 세션이 없으므로, 클라이언트에게 토큰 삭제를 유도하는 성공 응답을 반환합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "MEMBER200_5",
                    description = "로그아웃 성공"
            )
    })

    @PostMapping("/account")
    public ApiResponse<Void> logout() {
        // 프론트에서 로컬 토큰을 지우도록 성공 응답만 내려주도록
        return ApiResponse.onSuccess(MemberSuccessCode.MEMBER_LOGOUT_SUCCESS, null);
    }
}
