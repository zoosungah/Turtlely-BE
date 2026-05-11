package com.project.turtlely.domain.member.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.project.turtlely.domain.member.dto.LoginRequest;
import com.project.turtlely.domain.member.dto.LoginResponse;
import com.project.turtlely.domain.member.entity.Member;
import com.project.turtlely.domain.member.enums.Role;
import com.project.turtlely.domain.member.enums.SocialType;
import com.project.turtlely.domain.member.exception.MemberException;
import com.project.turtlely.domain.member.exception.code.MemberErrorCode;
import com.project.turtlely.domain.member.repository.MemberRepository;
import com.project.turtlely.global.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.Collections;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final MemberRepository memberRepository;
    private final JwtProvider jwtProvider;
    private final RedisService redisService;

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String googleClientId;

    // 일반 로그인 API 로직
    public LoginResponse login(LoginRequest request) {
        // DB에서 아이디로 회원 찾기
        // 회원 없으면 MEMBER_NOT_FOUND 에러를 던짐
        Member member = memberRepository.findByLoginId(request.loginId())
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

        // 비밀번호 맞는지 확인
        if (!member.getPassword().equals(request.password())) {
            throw new MemberException(MemberErrorCode.INVALID_PASSWORD);
        }

        String accessToken = jwtProvider.createAccessToken(member.getLoginId());
        String refreshToken = jwtProvider.createRefreshToken(member.getLoginId());

        // Redis에 리프레시 토큰 저장
        redisService.setValues(member.getLoginId(), refreshToken, Duration.ofDays(1));

        return new LoginResponse(accessToken, refreshToken, false);
    }

    // 구글 로그인 API 로직
    public LoginResponse googleLogin(String idToken) {
        // 구글 서버에 이 idToken이 진짜인지 검증 요청
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                .setAudience(Collections.singletonList(googleClientId))
                .build();

        try {
            GoogleIdToken token = verifier.verify(idToken);
            if (token == null) {
                throw new MemberException(MemberErrorCode.INVALID_GOOGLE_TOKEN); // 토큰이 가짜일 때 에러
            }

            // 검증된 토큰에서 이메일 정보 꺼냄
            GoogleIdToken.Payload payload = token.getPayload();
            String email = payload.getEmail();
            String socialId = payload.getSubject(); // 구글의 고유 식별자(socialId) 추출

            boolean isNewUser = false; // 신규 유저인지 판별

            // DB에서 해당 이메일로 가입된 회원이 있는지 확인
            Member member = memberRepository.findByLoginId(email).orElse(null);

            if (member == null) {
                isNewUser = true;
                String randomPassword = UUID.randomUUID().toString();

                member = Member.builder()
                        .loginId(email)
                        .socialId(socialId)
                        .password(randomPassword)
                        .role(Role.USER)
                        .socialType(SocialType.GOOGLE)
                        .build();

                member = memberRepository.save(member);
            }

            // JWT 토큰 발급
            String accessToken = jwtProvider.createAccessToken(member.getLoginId());
            String refreshToken = jwtProvider.createRefreshToken(member.getLoginId());

            // Redis에 리프레시 토큰 저장
            redisService.setValues(member.getLoginId(), refreshToken, Duration.ofDays(1));

            return new LoginResponse(accessToken, refreshToken, isNewUser);

        } catch (Exception e) {
            throw new MemberException(MemberErrorCode.LOGIN_INTERNAL_SERVER_ERROR);
        }
    }

    // 토큰 재발급 API 로직
    public LoginResponse reissue(String refreshToken) {
        // 리프레시 토큰 유효성 검증
        if (!jwtProvider.validateToken(refreshToken)) {
            throw new MemberException(MemberErrorCode.INVALID_REFRESH_TOKEN);
        }

        // 토큰에서 사용자 정보(loginId) 추출
        String loginId = jwtProvider.getLoginIdFromToken(refreshToken);

        // Redis에 저장된 토큰과 일치하는지 확인
        String savedToken = redisService.getValues(loginId);
        if (savedToken == null || !savedToken.equals(refreshToken)) {
            throw new MemberException(MemberErrorCode.INVALID_REFRESH_TOKEN);
        }

        // 사용자가 실제로 존재하는지 확인
        Member member = memberRepository.findByLoginId(loginId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

        String newAccessToken = jwtProvider.createAccessToken(member.getLoginId());
        String newRefreshToken = jwtProvider.createRefreshToken(member.getLoginId());

        redisService.setValues(member.getLoginId(), newRefreshToken, Duration.ofDays(1));

        return new LoginResponse(newAccessToken, newRefreshToken, false);
    }
}