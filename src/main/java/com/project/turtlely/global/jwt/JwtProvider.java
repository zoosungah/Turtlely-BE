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

    // Access Token 만들기
    public String createAccessToken(String loginId) {
        return createToken(loginId, accessTokenValidityTime);
    }

    // Refresh Token 만들기
    public String createRefreshToken(String loginId) {
        return createToken(loginId, refreshTokenValidityTime);
    }

    private String createToken(String loginId, long expireTime) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + expireTime);

        return Jwts.builder()
                .setSubject(loginId) // 출입증에 사용자 아이디 적기
                .setIssuedAt(now) // 발급 시간
                .setExpiration(validity) // 만료 시간
                .signWith(key, SignatureAlgorithm.HS256)
                .compact(); // 완성!
    }
}