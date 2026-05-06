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

import java.util.Collections;


@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final MemberRepository memberRepository;
    private final JwtProvider jwtProvider;

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String googleClientId;

    // 일반 로그인 API 로직
    public LoginResponse login(LoginRequest request) {
        // DB에서 아이디로 회원 찾기
        // 회원 없으면 MEMBER_NOT_FOUND 에러를 던짐
        Member member = memberRepository.findByLoginId(request.loginId())
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

        // 비밀번호 맞는지 확인하기
        if (!member.getPassword().equals(request.password())) {
            // 비밀번호가 다르면 에러 던짐
            throw new MemberException(MemberErrorCode.INVALID_PASSWORD);
        }

        String accessToken = jwtProvider.createAccessToken(member.getLoginId());
        String refreshToken = jwtProvider.createRefreshToken(member.getLoginId());

        return new LoginResponse(accessToken, refreshToken);
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

            // 검증된 토큰에서 진짜 이메일 정보 꺼냄
            GoogleIdToken.Payload payload = token.getPayload();
            String email = payload.getEmail();

            // DB에서 해당 이메일로 가입된 회원이 있는지 확인
            Member member = memberRepository.findByLoginId(email)
                    .orElseGet(() -> {
                        // 회원이 없다면? 자동으로 회원가입을 진행
                        Member newMember = Member.builder()
                                .loginId(email)
                                .nickname((String) payload.get("name")) // 구글 이름 사용
                                .role(Role.USER)
                                .socialType(SocialType.GOOGLE)
                                .build();
                        return memberRepository.save(newMember);
                    });

            // JWT 토큰 발급
            String accessToken = jwtProvider.createAccessToken(member.getLoginId());
            String refreshToken = jwtProvider.createRefreshToken(member.getLoginId());

            return new LoginResponse(accessToken, refreshToken);

        } catch (Exception e) {
            throw new MemberException(MemberErrorCode.LOGIN_INTERNAL_SERVER_ERROR);
        }
    }
}