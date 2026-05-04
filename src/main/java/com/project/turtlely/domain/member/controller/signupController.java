package com.project.turtlely.domain.member.controller;

import com.project.turtlely.domain.member.exception.code.MemberErrorCode;
import com.project.turtlely.domain.member.exception.code.MemberSuccessCode;
import com.project.turtlely.domain.member.service.signUpService;
import com.project.turtlely.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "사용자 회원가입", description = "회원가입 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth/signup")
public class signupController {

    private final signUpService signUpService;

    @Operation(summary = "ID 중복 확인 by 주성아 (개발 완료)",
            description = """
                회원가입 시 ID 중복 확인하는 API입니다.
                - `loginId`: 중복 여부를 확인할 아이디
                - 중복된 아이디가 존재하면 에러를 반환합니다.
                """
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "MEMBER200_1",
                    description = "사용 가능한 아이디"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "MEMBER400_1",
                    description = "이미 존재하는 아이디"
            )
    })

    @PostMapping("/check-id")
    public ApiResponse<Boolean> checkLoginId(@RequestParam String loginId) {
        boolean isDuplicate = signUpService.isLoginIdDuplicate(loginId);

        if (isDuplicate) {
            // 이미 존재하는 ID인 경우
            return ApiResponse.onFailure(MemberErrorCode.MEMBER_ID_ALREADY_EXISTS, true);
        }

        // 사용 가능한 ID인 경우 (중복이 아닌 경우)
        return ApiResponse.onSuccess(MemberSuccessCode.MEMBER_ID_AVAILABLE, false);
    }

}
