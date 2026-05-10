package com.project.turtlely.domain.member.controller;

import com.project.turtlely.domain.member.dto.MemberRequestDTO;
import com.project.turtlely.domain.member.exception.code.MemberErrorCode;
import com.project.turtlely.domain.member.exception.code.MemberSuccessCode;
import com.project.turtlely.domain.member.service.SignUpService;
import com.project.turtlely.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "사용자 회원가입", description = "회원가입 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth/signup")
public class SignupController {

    private final SignUpService signUpService;

    /**
     * ID 중복 확인
     */
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

    /**
     * 일반 회원가입
     */

    @Operation(summary = "일반 회원가입",
            description = "닉네임, 아이디, 비번, 전화번호를 받아 회원가입을 진행합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "MEMBER200_4",
                    description = "회원가입 완료"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "SMS400_2",
                    description = "SMS 인증 유효시간 만료"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "MEMBER400_1",
                    description = "이미 존재하는 아이디"
            )
    })
    @PostMapping("/signup")
    public ApiResponse<Void> signup(@RequestBody @Valid MemberRequestDTO.SignupDTO request) {
        signUpService.signup(request);
        return ApiResponse.onSuccess(MemberSuccessCode.MEMBER_SIGNUP_SUCCESS, null);
    }

}
