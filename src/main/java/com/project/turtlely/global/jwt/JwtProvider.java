package com.project.turtlely.global.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class JwtProvider {

    private final Key key;
    private final long accessTokenValidityTime;
    private final long refreshTokenValidityTime;

    public JwtProvider(
            @Value("${jwt.secret}") String secretKey,
            @Value("${jwt.access-token-validity-in-seconds}") long accessTokenValidityTime,
            @Value("${jwt.refresh-token-validity-in-seconds}") long refreshTokenValidityTime) {
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.accessTokenValidityTime = accessTokenValidityTime * 1000;
        this.refreshTokenValidityTime = refreshTokenValidityTime * 1000;
    }

    // loginId 대신 DB PK인 memberId를 받아서 Access Token 만들기
    public String createAccessToken(Long memberId) {
        return createToken(String.valueOf(memberId), accessTokenValidityTime);
    }

    // loginId 대신 DB PK인 memberId를 받아서 Refresh Token 만들기
    public String createRefreshToken(Long memberId) {
        return createToken(String.valueOf(memberId), refreshTokenValidityTime);
    }

    // subject로 문자열로 변환된 memberId가 들어옴
    private String createToken(String subject, long expireTime) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + expireTime);

        return Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    //  토큰에서 식별자인 memberId를 Long 타입으로 바로 꺼내기
    public Long getMemberIdFromToken(String token) {
        String subject = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
        return Long.valueOf(subject); // 문자열 "1"을 숫자 1L로 변환하여 반환
    }

    // 토큰 유효성 검증하기
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}