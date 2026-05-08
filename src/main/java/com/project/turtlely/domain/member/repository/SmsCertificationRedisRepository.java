package com.project.turtlely.domain.member.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;
import java.time.Duration;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class SmsCertificationRedisRepository {

    // Redis에 저장될 때 "SmsCode:01012345678" 형식으로 저장됩니다.
    private static final String KEY_PREFIX = "SmsCode:";
    private final StringRedisTemplate redisTemplate;

    /** 인증번호 저장 (유효시간 설정) */
    public void saveCertification(String phoneNumber, String certificationNumber, Duration ttl) {
        redisTemplate.opsForValue()
                .set(makeKey(phoneNumber), certificationNumber, ttl);
    }

    /** 인증번호 조회 */
    public Optional<String> getCertification(String phoneNumber) {
        return Optional.ofNullable(
                redisTemplate.opsForValue().get(makeKey(phoneNumber))
        );
    }

    /** 인증번호 삭제 (검증 완료 후) */
    public void deleteCertification(String phoneNumber) {
        redisTemplate.delete(makeKey(phoneNumber));
    }

    private String makeKey(String phoneNumber) {
        return KEY_PREFIX + phoneNumber;
    }

}
