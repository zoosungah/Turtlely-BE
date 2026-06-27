package com.project.turtlely.domain.member.service;

import com.project.turtlely.domain.member.entity.Member;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class PrincipalDetails implements UserDetails {

    private final Member member; // 🌟 우리 프로젝트의 Member 엔티티를 품어줍니다.

    public PrincipalDetails(Member member) {
        this.member = member;
    }

    public Member getMember() {
        return member;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + member.getRole().name()));
    }

    @Override
    public String getPassword() { return member.getPassword(); }

    @Override
    public String getUsername() { return member.getLoginId(); }
}