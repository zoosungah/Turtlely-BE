package com.project.turtlely.domain.member.controller;

import com.project.turtlely.domain.member.dto.AccountResponseDTO;
import com.project.turtlely.domain.member.dto.MemberRequestDTO;
import com.project.turtlely.domain.member.dto.SmsRequestDTO;
import com.project.turtlely.domain.member.exception.code.MemberSuccessCode;
import com.project.turtlely.domain.member.service.AccountService;
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

@Tag(name = "사용자 계정 관리", description = "사용자 계정 관리 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class AccountController {
    private final AccountService accountService;

    /**
     * 로그아웃 API
     */
    @Operation(
            summary = "로그아웃 API by 주성아(개발 완료)",
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

    /**
     * 아이디 찾기 API
     */
    @Operation(summary = "아이디 찾기 API by 주성아(개발 완료)",
            description = "전화번호 인증 후 사용자 ID를 반환합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "MEMBER200_6",
                    description = "아이디 찾기에 성공하였습니다."
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "MEMBER404_1",
                    description = "해당 전화번호로 가입된 회원이 없습니다."
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "SMS400_1",
                    description = "인증번호가 일치하지 않습니다."
            )
    })
    @PostMapping("/account/id")
    public ApiResponse<AccountResponseDTO.FindIdResultDTO> findId(
            @RequestBody @Valid SmsRequestDTO.SmsSendDTO request) {

        AccountResponseDTO.FindIdResultDTO result = accountService.findId(request.getPhoneNumber());
        return ApiResponse.onSuccess(MemberSuccessCode.MEMBER_FIND_ID_SUCCESS, result);
    }


    /**
     * 비밀번호 찾기(임시 비밀번호 발급)
     */
    @Operation(summary = "비밀번호 찾기 API by 주성아(개발 완료)", description = "전화번호 인증 후 임시 비밀번호를 생성하여 SMS로 발급합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "AUTH_FIND_PW_SUCCESS", description = "임시 비밀번호가 발송되었습니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "MEMBER404_1", description = "해당 전화번호로 가입된 회원이 없습니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "SMS400_1", description = "인증번호가 일치하지 않습니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "SMS500_1", description = "SMS 발송 시스템 오류")
    })
    @PostMapping("/account/pw")
    public ApiResponse<Void> findPw(
            @RequestBody @Valid SmsRequestDTO.SmsSendDTO request) {

        accountService.findPwAndSendTemporaryPassword(request.getPhoneNumber());
        return ApiResponse.onSuccess(MemberSuccessCode.MEMBER_FIND_PW_SUCCESS, null);
    }
}
