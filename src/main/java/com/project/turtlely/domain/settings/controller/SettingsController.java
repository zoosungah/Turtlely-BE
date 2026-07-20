package com.project.turtlely.domain.settings.controller;

import com.project.turtlely.domain.settings.dto.SettingsRequestDto;
import com.project.turtlely.domain.settings.exception.code.SettingsSuccessCode;
import com.project.turtlely.domain.settings.service.SettingsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import com.project.turtlely.domain.member.service.PrincipalDetails;
import com.project.turtlely.global.apiPayload.ApiResponse;

import java.util.Map;

@Tag(name = "회원 설정 관리", description = "회원 설정 및 프로필 관리 API")
@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class SettingsController {

    private final SettingsService settingsService;

    /**
     * 닉네임 조회 API
     */
    @Operation(summary = "닉네임 조회 API by 주성아(개발 완료)", description = "현재 로그인한 사용자의 닉네임을 조회합니다.")
    @GetMapping("/nickname")
    public ApiResponse<Map<String, String>> getNickname(
            @AuthenticationPrincipal PrincipalDetails principalDetails) {

        Long memberId = principalDetails.getMember().getMemberId();
        String nickname = settingsService.getNickname(memberId);
        return ApiResponse.onSuccess(SettingsSuccessCode.NICKNAME_GET_SUCCESS, Map.of("nickname", nickname));
    }

    /**
     * 아이디 조회 API
     */
    @Operation(summary = "아이디 조회 API by 주성아(개발 완료)", description = "현재 로그인한 사용자의 아이디(계정)를 조회합니다.")
    @GetMapping("/id")
    public ApiResponse<Map<String, String>> getLoginId(
            @AuthenticationPrincipal PrincipalDetails principalDetails) {

        Long memberId = principalDetails.getMember().getMemberId();
        String loginId = settingsService.getLoginId(memberId);
        return ApiResponse.onSuccess(SettingsSuccessCode.LOGIN_ID_GET_SUCCESS, Map.of("loginId", loginId));
    }

    /**
     * 닉네임 변경 API
     */
    @Operation(summary = "닉네임 변경 API by 주성아(개발 완료)", description = "사용자의 닉네임을 변경합니다.")
    @PatchMapping("/nickname")
    public ApiResponse<Map<String, String>> updateNickname(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @Valid @RequestBody SettingsRequestDto.UpdateNickname request) {

        Long memberId = principalDetails.getMember().getMemberId();
        settingsService.updateNickname(memberId, request.getNickname());
        return ApiResponse.onSuccess(SettingsSuccessCode.NICKNAME_UPDATE_SUCCESS, Map.of("message", "닉네임이 성공적으로 변경되었습니다."));
    }

    /**
     * 비밀번호 재설정 API
     */
    @Operation(summary = "비밀번호 재설정 API by 주성아(개발 완료)", description = "현재 비밀번호 검증 후 새 비밀번호로 변경합니다.")
    @PatchMapping("/password")
    public ApiResponse<Map<String, String>> resetPassword(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @Valid @RequestBody SettingsRequestDto.ResetPassword request) {

        Long memberId = principalDetails.getMember().getMemberId();
        settingsService.resetPassword(memberId, request.getCurrentPassword(), request.getNewPassword());
        return ApiResponse.onSuccess(SettingsSuccessCode.PASSWORD_RESET_SUCCESS, Map.of("message", "비밀번호가 성공적으로 변경되었습니다."));
    }

    /**
     * 회원 탈퇴 API
     */
    @Operation(
            summary = "회원 탈퇴 API by 주성아(개발 완료)",
            description = "현재 로그인한 사용자의 계정 및 관련 정보를 삭제(탈퇴)합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "MEMBER200_6",
                    description = "회원 탈퇴 성공"
            )
    })
    @DeleteMapping("/withdraw")
    public ApiResponse<Map<String, String>> withdraw(
            @AuthenticationPrincipal PrincipalDetails principalDetails) {

        Long memberId = principalDetails.getMember().getMemberId();
        settingsService.deleteMember(memberId);

        return ApiResponse.onSuccess(SettingsSuccessCode.WITHDRAWAL_SUCCESS, Map.of("message", "성공적으로 탈퇴되었습니다."));
    }


}