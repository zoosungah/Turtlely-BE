package com.project.turtlely.domain.measurement.jwt;

import com.project.turtlely.domain.member.entity.Member;
import com.project.turtlely.domain.member.repository.MemberRepository;
import com.project.turtlely.global.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
@RequiredArgsConstructor
public class MeasurementJwtInterceptor implements HandlerInterceptor {

    private final JwtProvider jwtProvider;
    private final MemberRepository memberRepository;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String bearerToken = request.getHeader("Authorization");

        if (bearerToken == null || !bearerToken.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"isSuccess\":false,\"code\":\"AUTH_TOKEN_INVALID\",\"message\":\"인증 실패\"}");
            return false;
        }

        String token = bearerToken.substring(7);

        if (!jwtProvider.validateToken(token)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"isSuccess\":false,\"code\":\"AUTH_TOKEN_INVALID\",\"message\":\"유효하지 않거나 만료된 인증 토큰입니다.\"}");
            return false;
        }

        Member member = null;
        try {
            String loginIdStr = jwtProvider.getLoginIdFromToken(token);

            if (loginIdStr != null) {
                member = memberRepository.findByLoginId(loginIdStr).orElse(null);
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"isSuccess\":false,\"code\":\"AUTH_TOKEN_INVALID\",\"message\":\"인증 정보 분석에 실패했습니다.\"}");
            return false;
        }

        if (member == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"isSuccess\":false,\"code\":\"AUTH_TOKEN_INVALID\",\"message\":\"존재하지 않는 회원 정보입니다.\"}");
            return false;
        }

        request.setAttribute("member", member);
        return true;
    }
}