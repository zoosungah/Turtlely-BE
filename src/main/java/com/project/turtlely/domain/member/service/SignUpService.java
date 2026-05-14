package com.project.turtlely.domain.member.service;

import com.project.turtlely.domain.member.dto.LoginResponse;
import com.project.turtlely.domain.member.dto.MemberRequestDTO;
import com.project.turtlely.domain.member.entity.Member;
import com.project.turtlely.domain.member.enums.Role;
import com.project.turtlely.domain.member.enums.SocialType;
import com.project.turtlely.domain.member.exception.MemberException;
import com.project.turtlely.domain.member.exception.code.MemberErrorCode;
import com.project.turtlely.domain.member.exception.code.SmsErrorCode;
import com.project.turtlely.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.project.turtlely.global.jwt.JwtProvider;

import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SignUpService {
    private final MemberRepository memberRepository;
    private final StringRedisTemplate redisTemplate;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final RedisService redisService;

    private final String VERIFIED_PREFIX = "sms:verified:";

    /**
     * ID 중복 확인
     * @return 존재하면 true(중복), 없으면 false(사용 가능)
     */
    public boolean isLoginIdDuplicate(String loginId) {
        return memberRepository.existsByLoginId(loginId);
    }

    @Transactional
    public void signup(MemberRequestDTO.SignupDTO request) {
        // 1. 아이디 중복 확인
        if (memberRepository.existsByLoginId(request.getLoginId())) {
            throw new MemberException(MemberErrorCode.MEMBER_ID_ALREADY_EXISTS);
        }

        // 2. SMS 인증 유효시간 확인
        validateSmsVerification(request.getPhoneNumber());

        // 3. 비밀번호 암호화 후 저장
        Member member = Member.builder()
                .loginId(request.getLoginId())
                .password(passwordEncoder.encode(request.getPassword()))
                .nickname(request.getNickname())
                .phoneNumber(request.getPhoneNumber())
                .socialType(SocialType.LOCAL)
                .role(Role.USER)
                .build();

        memberRepository.save(member);

        // 4. 인증 정보 삭제 및 정리
        redisTemplate.delete(VERIFIED_PREFIX + request.getPhoneNumber());
    }

    @Transactional
    public LoginResponse signupSocial(MemberRequestDTO.SocialSignupDTO request) {
        // 1. 이미 해당 소셜 계정으로 가입된 번호가 있는지 확인 (선택)
        if (memberRepository.existsBySocialId(request.getSocialId())) {
            throw new MemberException(MemberErrorCode.MEMBER_ALREADY_SOCIAL_REGISTERED);
        }

        // 2. SMS 인증 확인
        validateSmsVerification(request.getPhoneNumber());

        // 3. 레디스에서 사용자가 넣어둔 이메일 꺼내오기
        String email = redisTemplate.opsForValue().get("google:email:" + request.getSocialId());
        if (email == null) {
            throw new MemberException(MemberErrorCode.MEMBER_NOT_FOUND);
        }

        String randomPassword = UUID.randomUUID().toString();

        // 4. 저장
        Member member = Member.builder()
                .loginId(email)
                .password(passwordEncoder.encode(randomPassword))
                .nickname(request.getNickname())
                .socialId(request.getSocialId())
                .phoneNumber(request.getPhoneNumber())
                .socialType(SocialType.GOOGLE)
                .role(Role.USER)
                .build();
        memberRepository.save(member);


        //회원가입 성공-> 토큰 발행
        String accessToken = jwtProvider.createAccessToken(member.getLoginId());
        String refreshToken = jwtProvider.createRefreshToken(member.getLoginId());

        // 리프레시 토큰 Redis 저장
        redisService.setValues(member.getLoginId(), refreshToken, Duration.ofDays(1));

        redisTemplate.delete(VERIFIED_PREFIX + request.getPhoneNumber());
        redisTemplate.delete("google:email:" + request.getSocialId());

        return new LoginResponse(accessToken, refreshToken, false, null);
    }

    // 인증 시간 만료됐는지 확인
    private void validateSmsVerification(String phoneNumber) {
        String isVerified = redisTemplate.opsForValue().get(VERIFIED_PREFIX + phoneNumber);
        if (isVerified == null || !isVerified.equals("true")) {
            throw new MemberException(SmsErrorCode.SMS_BAD_REQUEST);
        }
    }
}
