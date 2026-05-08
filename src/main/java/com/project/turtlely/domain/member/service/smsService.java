package com.project.turtlely.domain.member.service;

import com.project.turtlely.domain.member.exception.code.SmsErrorCode;
import com.project.turtlely.domain.member.repository.MemberRepository;
import com.project.turtlely.domain.member.repository.SmsCertificationRedisRepository;
import jdk.jshell.spi.ExecutionControl;

import java.time.Duration;
import java.util.Random;
import org.springframework.beans.factory.annotation.Value;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import com.project.turtlely.global.exception.GeneralException;
import java.util.concurrent.TimeUnit;

@Service
public class smsService {
    private final DefaultMessageService messageService;
    private final String fromNumber;
    private final StringRedisTemplate redisTemplate; // Redis 연결용
    private final MemberRepository memberRepository;
    private final SmsCertificationRedisRepository redisRepository;

    // 인증번호 저장용 키
    private static final String SMS_PREFIX = "sms:";
    // 인증 완료 상태 저장용 키
    private final String VERIFIED_PREFIX = "sms:verified:";

    public smsService(
            @Value("${coolsms.api.key}") String apiKey,
            @Value("${coolsms.api.secret}") String apiSecret,
            @Value("${coolsms.from}") String fromNumber,
            StringRedisTemplate redisTemplate,
            MemberRepository memberRepository,
            SmsCertificationRedisRepository redisRepository) {
        this.messageService = NurigoApp.INSTANCE.initialize(apiKey, apiSecret, "https://api.coolsms.co.kr");
        this.fromNumber = fromNumber;
        this.redisTemplate = redisTemplate;
        this.memberRepository = memberRepository;
        this.redisRepository = redisRepository;
    }

    /**
     * 인증번호를 생성하고 발송하며, Redis에 5분간 저장
     * 이미 가입한 전화번호라면 인증번호 생성 불가
     */
    public String sendVerificationSms(String phoneNumber) {
        if (memberRepository.existsByPhoneNumber(phoneNumber)) {
            return "ALREADY_EXISTS";
        }
        String verificationCode = String.format("%04d", new Random().nextInt(10000));

        Message message = new Message();
        message.setFrom(fromNumber);
        message.setTo(phoneNumber);
        message.setText("[Turtlely] 인증번호는 [" + verificationCode + "] 입니다.");

        try {
            this.messageService.sendOne(new SingleMessageSendingRequest(message));

            // Redis에 저장 (Key: sms:010..., Value: 인증번호, TTL: 5분)
//            redisTemplate.opsForValue().set(
//                    SMS_PREFIX + phoneNumber,
//                    verificationCode,
//                    5,
//                    TimeUnit.MINUTES
//            );
            redisRepository.saveCertification(phoneNumber, verificationCode, Duration.ofMinutes(5));
            return "SUCCESS";

        } catch (Exception e) {
            throw new GeneralException(SmsErrorCode.SMS_INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 입력받은 번호가 Redis에 저장된 번호와 일치하는지 확인 + 인증상태 15분간 저장
     */
    public String verifyCode(String phoneNumber, String inputCode) {
//        String savedCode = redisTemplate.opsForValue().get(SMS_PREFIX + phoneNumber);
        String savedCode = redisRepository.getCertification(phoneNumber).orElse(null);

        // 1. 인증번호가 만료되었을 때
        if (savedCode == null) {
            return "EXPIRED";
        }

        // 2. 인증번호가 틀렸을 때
        if (!savedCode.equals(inputCode)) {
            return "MISMATCH";
        }

        // 3. 인증 성공 시 redis에서 데이터 삭제 + 인증 완료 상태를 15분간 저장
        redisTemplate.delete(SMS_PREFIX + phoneNumber);
        redisTemplate.opsForValue().set(VERIFIED_PREFIX + phoneNumber, "true", 15, TimeUnit.MINUTES);

        return "SUCCESS";
    }
}
