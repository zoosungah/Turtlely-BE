package com.project.turtlely.domain.member.service;

import com.project.turtlely.domain.member.entity.Member;
import com.project.turtlely.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String loginId) throws UsernameNotFoundException {
        Member member = memberRepository.findByLoginId(loginId)
                .orElseThrow(() -> new UsernameNotFoundException("해당 유저를 찾을 수 없습니다: " + loginId));

        // 2. 스프링 시큐리티 규격에 맞는 UserDetails(여기서는 기본 User 객체)를 생성해서 반환
//        return User.builder()
//                .username(member.getLoginId())
//                .password(member.getPassword())
//                .roles(member.getRole().name()) // USER, ADMIN 등
//                .build();
        return new PrincipalDetails(member);
    }
}