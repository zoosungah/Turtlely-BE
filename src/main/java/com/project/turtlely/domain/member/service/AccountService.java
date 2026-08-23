package com.project.turtlely.domain.member.service;

import com.project.turtlely.domain.member.dto.AccountResponseDTO;
import com.project.turtlely.domain.member.entity.Member;
import com.project.turtlely.domain.member.exception.MemberException;
import com.project.turtlely.domain.member.exception.code.MemberErrorCode;
import com.project.turtlely.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AccountService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final SmsService smsService;

    /**
     * 아이디 찾기
     */
    public AccountResponseDTO.FindIdResultDTO findId(String phoneNumber) {
        Member member = memberRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

        return AccountResponseDTO.FindIdResultDTO.builder()
                .loginId(member.getLoginId()) // 마스킹 없이 그대로 반환
                .build();
    }

    /**
     * 비밀번호 찾기
     */
    @Transactional
    public void findPwAndSendTemporaryPassword(String phoneNumber) {
        // 1. 유저 확인
        Member member = memberRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

        // 2. 임시 비밀번호 생성
        String tempPassword = generateTempPassword();

        // 3. DB 비밀번호 업데이트(암호화해서)
        member.updatePassword(passwordEncoder.encode(tempPassword));

        // 4. SMS 서비스 호출
        smsService.sendTemporaryPasswordSms(phoneNumber, tempPassword);
    }

    private String generateTempPassword() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    @Transactional
    public void updateFcmToken(Long memberId, String fcmToken) {
        // 1. 다른 계정에 이미 등록된 동일한 FCM 토큰을 null로 초기화
        if (fcmToken != null && !fcmToken.isBlank()) {
            memberRepository.clearExistingFcmToken(fcmToken);
        }

        // 2. 현재 로그인한 회원 엔티티 조회 및 FCM 토큰 갱신
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));
        member.updateFcmToken(fcmToken);
    }
}