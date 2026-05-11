package com.project.turtlely.domain.member.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.project.turtlely.domain.member.dto.LoginRequest;
import com.project.turtlely.domain.member.dto.LoginResponse;
import com.project.turtlely.domain.member.entity.Member;
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
        Member member = memberRepository.findByLoginId(request.loginId())
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

        if (!member.getPassword().equals(request.password())) {
            throw new MemberException(MemberErrorCode.INVALID_PASSWORD);
        }

        String accessToken = jwtProvider.createAccessToken(member.getLoginId());
        String refreshToken = jwtProvider.createRefreshToken(member.getLoginId());

        redisService.setValues(member.getLoginId(), refreshToken, Duration.ofDays(1));

        return new LoginResponse(accessToken, refreshToken, false, null);
    }

    // 구글 로그인 API 로직
    public LoginResponse googleLogin(String idToken) {
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                .setAudience(Collections.singletonList(googleClientId))
                .build();

        try {
            GoogleIdToken token = verifier.verify(idToken);
            if (token == null) {
                throw new MemberException(MemberErrorCode.INVALID_GOOGLE_TOKEN);
            }

            GoogleIdToken.Payload payload = token.getPayload();
            String email = payload.getEmail();
            String socialId = payload.getSubject();

            Member member = memberRepository.findByLoginId(email).orElse(null);

            if (member == null) {
                // 구글 이메일을 레디스에 1일 동안 임시보관
                redisService.setValues("google:email:" + socialId, email, Duration.ofDays(1));

                // 신규 유저이므로 (토큰 없음, 토큰 없음, isNewUser=true, socialId 전달)
                return new LoginResponse(null, null, true, socialId);
            }

            // 기존 유저일때만 실행
            String accessToken = jwtProvider.createAccessToken(member.getLoginId());
            String refreshToken = jwtProvider.createRefreshToken(member.getLoginId());

            redisService.setValues(member.getLoginId(), refreshToken, Duration.ofDays(1));

            return new LoginResponse(accessToken, refreshToken, false, null);

        } catch (Exception e) {
            throw new MemberException(MemberErrorCode.LOGIN_INTERNAL_SERVER_ERROR);
        }
    }

    // 토큰 재발급 API 로직
    public LoginResponse reissue(String refreshToken) {
        if (!jwtProvider.validateToken(refreshToken)) {
            throw new MemberException(MemberErrorCode.INVALID_REFRESH_TOKEN);
        }

        String loginId = jwtProvider.getLoginIdFromToken(refreshToken);

        String savedToken = redisService.getValues(loginId);
        if (savedToken == null || !savedToken.equals(refreshToken)) {
            throw new MemberException(MemberErrorCode.INVALID_REFRESH_TOKEN);
        }

        Member member = memberRepository.findByLoginId(loginId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

        String newAccessToken = jwtProvider.createAccessToken(member.getLoginId());
        String newRefreshToken = jwtProvider.createRefreshToken(member.getLoginId());

        redisService.setValues(member.getLoginId(), newRefreshToken, Duration.ofDays(1));

        return new LoginResponse(newAccessToken, newRefreshToken, false, null);
    }
}