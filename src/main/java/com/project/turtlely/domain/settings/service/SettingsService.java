package com.project.turtlely.domain.settings.service;

import com.project.turtlely.domain.member.entity.Member;
import com.project.turtlely.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SettingsService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    // 1. 닉네임 조회
    public String getNickname(Long memberId) {
        return getMember(memberId).getNickname();
    }

    // 2. 아이디(이메일/계정) 조회
    public String getLoginId(Long memberId) {
        return getMember(memberId).getLoginId();
    }

    // 3. 닉네임 변경
    @Transactional
    public void updateNickname(Long memberId, String newNickname) {
        Member member = getMember(memberId);

        if (memberRepository.existsByNickname(newNickname)) {
            throw new IllegalArgumentException("이미 사용 중인 닉네임입니다.");
        }

        member.updateNickname(newNickname);
    }

    // 4. 비밀번호 재설정
    @Transactional
    public void resetPassword(Long memberId, String currentPassword, String newPassword) {
        Member member = getMember(memberId);

        if (!passwordEncoder.matches(currentPassword, member.getPassword())) {
            throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
        }

        member.updatePassword(passwordEncoder.encode(newPassword));
    }

    private Member getMember(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
    }

    /**
     * 회원 탈퇴
     */
    @Transactional
    public void deleteMember(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        memberRepository.delete(member);
    }
}
