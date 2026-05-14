package com.project.turtlely.domain.member.service;

import com.project.turtlely.domain.member.dto.LoginRequest;
import com.project.turtlely.domain.member.dto.LoginResponse;
import com.project.turtlely.domain.member.entity.Member;
import com.project.turtlely.domain.member.exception.MemberException;
import com.project.turtlely.domain.member.exception.code.MemberErrorCode;
import com.project.turtlely.domain.member.repository.MemberRepository;
import com.project.turtlely.global.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final MemberRepository memberRepository;
    private final JwtProvider jwtProvider;
    private final RedisService redisService;
    private final PasswordEncoder passwordEncoder;

    // 일반 로그인 API 로직
    public LoginResponse login(LoginRequest request) {
        Member member = memberRepository.findByLoginId(request.loginId())
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

        if (!passwordEncoder.matches(request.password(), member.getPassword())) {
            throw new MemberException(MemberErrorCode.INVALID_PASSWORD);
        }

        String accessToken = jwtProvider.createAccessToken(member.getLoginId());
        String refreshToken = jwtProvider.createRefreshToken(member.getLoginId());

        redisService.setValues(member.getLoginId(), refreshToken, Duration.ofDays(1));

        return new LoginResponse(accessToken, refreshToken, false, null);
    }

    // 구글 로그인 API 로직
    public LoginResponse googleLogin(String googleAccessToken) {

        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(googleAccessToken);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    "https://www.googleapis.com/oauth2/v3/userinfo",
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<>() {}
            );

            Map<String, Object> userInfo = response.getBody();
            if (userInfo == null) {
                throw new MemberException(MemberErrorCode.INVALID_GOOGLE_TOKEN);
            }

            String email = (String) userInfo.get("email");
            String socialId = (String) userInfo.get("sub");

            Member member = memberRepository.findByLoginId(email).orElse(null);

            if (member == null) {
                redisService.setValues("google:email:" + socialId, email, Duration.ofDays(1));

                return new LoginResponse(null, null, true, socialId);
            }

            String jwtAccessToken = jwtProvider.createAccessToken(member.getLoginId());
            String jwtRefreshToken = jwtProvider.createRefreshToken(member.getLoginId());

            redisService.setValues(member.getLoginId(), jwtRefreshToken, Duration.ofDays(1));

            return new LoginResponse(jwtAccessToken, jwtRefreshToken, false, null);
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