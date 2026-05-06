package com.project.turtlely.domain.member.controller;

import com.project.turtlely.domain.member.dto.LoginRequest;
import com.project.turtlely.domain.member.dto.LoginResponse;
import com.project.turtlely.domain.member.exception.code.MemberSuccessCode;
import com.project.turtlely.domain.member.service.AuthService;
import com.project.turtlely.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "인증/권한", description = "로그인 및 토큰 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class LoginController {

    private final AuthService authService;

    @Operation(summary = "일반 로그인 API by 김승연(개발완료)",
            description = """
                사용자가 아이디와 비밀번호를 입력해 로그인을 진행합니다.
                - 성공 시 `accessToken`과 `refreshToken`을 반환합니다.
                """
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "LOGIN_SUCCESS",
                    description = "로그인에 성공하였습니다."
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "LOGIN_INVALID_PARAMETER", // 400 에러
                    description = "아이디/비밀번호 형식 오류"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "MEMBER_NOT_FOUND", // 401 에러
                    description = "일치하는 회원 정보가 없을 때"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "LOGIN_INTERNAL_SERVER_ERROR", // 500 에러
                    description = "서버 내부 오류"
            )
    })
    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@RequestBody LoginRequest request) {

        LoginResponse realResponse = authService.login(request);

        return ApiResponse.onSuccess(MemberSuccessCode.LOGIN_SUCCESS, realResponse);
    }

    @Operation(summary = "구글 로그인 API by 김승연(개발완료)",
            description = """
                구글 OAuth2 인증을 통해 로그인을 진행합니다.
                - 프론트엔드에서 구글로부터 받은 `idToken`을 넘겨주면, 백엔드에서 검증 후 토큰을 발급합니다.
                """
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "LOGIN_SUCCESS",
                    description = "구글 로그인에 성공하였습니다."
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "LOGIN_INTERNAL_SERVER_ERROR",
                    description = "서버 내부 오류"
            )
    })

    @PostMapping("/login/google")
    public ApiResponse<LoginResponse> googleLogin(@RequestBody String idToken) {

        // 구글로그인
        LoginResponse realResponse = authService.googleLogin(idToken);

        return ApiResponse.onSuccess(MemberSuccessCode.LOGIN_SUCCESS, realResponse);
    }
}