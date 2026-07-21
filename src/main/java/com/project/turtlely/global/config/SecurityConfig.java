package com.project.turtlely.global.config;

import com.project.turtlely.global.jwt.JwtAuthenticationFilter;
import com.project.turtlely.global.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtProvider jwtProvider;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // CSRF 비활성화 (Postman/Swagger 테스트 시 403 방지)
                .csrf(csrf -> csrf.disable())

                .formLogin(form -> form.disable())

                // 경로별 권한 설정
                .authorizeHttpRequests(auth -> auth
                        // 로그인 없이 허용 가능한 경로
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**",
                                "/swagger-resources/**",
                                "/webjars/**",
                                "/error",
                                "/auth/**",
                                "/api/sms/**",
                                "/api/account/**",
                                "/api/admin/exercise/**",
                                "/api/exercise/**"
//                                "/api/daily/**",
//                                "/api/monthly/**"
                        ).permitAll()

                        // HW fastapi에서 쏘는 post 요청만 로그인 없이 허용
                        .requestMatchers(HttpMethod.POST, "/api/daily/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/monthly/**").permitAll()
                        // 그 외 모든 요청은 인증 필요
                        .anyRequest().authenticated()
                );

        // JWT 인증 필터 적용
        http.addFilterBefore(new JwtAuthenticationFilter(jwtProvider),
                UsernamePasswordAuthenticationFilter.class);

        return http.build();

    }


    /**
     * 회원가입 중 회원정보 저장 시 사용자 비밀번호 암호화
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}