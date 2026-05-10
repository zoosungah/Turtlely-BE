package com.project.turtlely.domain.member.service;

import com.project.turtlely.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SignUpService {
    private final MemberRepository memberRepository;

    /**
     * ID 중복 확인
     * @return 존재하면 true(중복), 없으면 false(사용 가능)
     */
    public boolean isLoginIdDuplicate(String loginId) {
        return memberRepository.existsByLoginId(loginId);
    }
}
