package com.project.turtlely.domain.member.controller;

import com.project.turtlely.domain.member.dto.GoogleLoginRequest;
import com.project.turtlely.domain.member.dto.LoginRequest;
import com.project.turtlely.domain.member.dto.LoginResponse;
import com.project.turtlely.domain.member.dto.ReissueRequest;
import com.project.turtlely.domain.member.exception.code.MemberSuccessCode;
import com.project.turtlely.domain.member.service.AuthService;
import com.project.turtlely.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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

    @Operation(summary = "일반 로그인 API",
            description = "사용자가 아이디와 비밀번호를 입력해 로그인을 진행합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "MEMBER200_2", description = "로그인에 성공하였습니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "MEMBER400_1", description = "아이디/비밀번호 형식 오류"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "MEMBER401_1", description = "일치하는 회원 정보가 없을 때"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "MEMBER500_1", description = "서버 내부 오류")
    })
    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@RequestBody @Valid LoginRequest request) {
        LoginResponse realResponse = authService.login(request);
        return ApiResponse.onSuccess(MemberSuccessCode.LOGIN_SUCCESS, realResponse);
    }

    @Operation(summary = "구글 로그인 API",
            description = """
                구글 OAuth2 인증을 통해 로그인을 진행합니다.
                - 프론트엔드에서 구글로부터 받은 `idToken`을 넘겨주면 검증 후 토큰을 발급합니다.
                - 응답의 `isNewUser`가 true일 경우, 프론트에서 닉네임 설정 페이지로 라우팅해야 합니다.
                """
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "MEMBER200_2", description = "구글 로그인에 성공하였습니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "MEMBER500_1", description = "서버 내부 오류")
    })
    @PostMapping("/login/google")
    public ApiResponse<LoginResponse> googleLogin(@RequestBody @Valid GoogleLoginRequest request) {
        LoginResponse realResponse = authService.googleLogin(request.idToken());
        return ApiResponse.onSuccess(MemberSuccessCode.LOGIN_SUCCESS, realResponse);
    }

    @Operation(summary = "토큰 재발급 API",
            description = "만료된 Access Token을 Refresh Token을 통해 재발급합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "MEMBER200_3", description = "토큰이 성공적으로 재발급되었습니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "MEMBER401_2", description = "토큰이 만료되었을 때")
    })
    @PostMapping("/reissue")
    public ApiResponse<LoginResponse> reissue(@RequestBody @Valid ReissueRequest request) {
        LoginResponse realResponse = authService.reissue(request.refreshToken());
        return ApiResponse.onSuccess(MemberSuccessCode.TOKEN_REISSUE_SUCCESS, realResponse);
    }
}