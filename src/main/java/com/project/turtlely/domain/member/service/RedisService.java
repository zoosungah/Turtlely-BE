package com.project.turtlely.domain.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RedisService {

    private final StringRedisTemplate stringRedisTemplate;

    /**
     * 1. 레디스에 데이터 저장 (리프레시 토큰 저장용)
     * @param key 저장할 키 (유저의 로그인 이메일)
     * @param data 저장할 값 (리프레시 토큰 값)
     * @param duration 만료 시간 (예: Duration.ofDays(14))
     */
    public void setValues(String key, String data, Duration duration) {
        stringRedisTemplate.opsForValue().set(key, data, duration);
    }

    /**
     * 2. 레디스에서 데이터 조회 (토큰 재발급 시 검증용)
     * @param key 찾을 키 (이메일)
     * @return 저장된 토큰 값 (데이터가 없으면 null 반환)
     */
    public String getValues(String key) {
        return stringRedisTemplate.opsForValue().get(key);
    }

    /**
     * 3. 레디스에서 데이터 삭제 (로그아웃 시 사용)
     * @param key 지울 키 (이메일)
     */
    public void deleteValues(String key) {
        stringRedisTemplate.delete(key);
    }
}