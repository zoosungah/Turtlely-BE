package com.project.turtlely.global.jwt;

import com.project.turtlely.domain.member.entity.Member;
import com.project.turtlely.domain.member.repository.MemberRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final MemberRepository memberRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 1. 헤더에서 Authorization 추출
        String bearerToken = request.getHeader("Authorization");

        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            String token = bearerToken.substring(7);

            // 2. 토큰이 유효하다면 유저 파싱
            if (jwtProvider.validateToken(token)) {
                Long memberId = jwtProvider.getMemberIdFromToken(token);

                Member member = memberRepository.findById(memberId).orElse(null);

                if (member != null) {
                    // 3. 컨트롤러의 @RequestAttribute("member")가 읽을 수 있도록 request에 탑재!
                    request.setAttribute("member", member);

                    // 스프링 시큐리티 컨텍스트에 인증 객체 저장
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            member,
                            null,
                            List.of(new SimpleGrantedAuthority("ROLE_" + member.getRole().name()))
                    );
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        }

        filterChain.doFilter(request, response);
    }
}