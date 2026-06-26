package com.project.turtlely.domain.measurement.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class MeasurementWebConfig implements WebMvcConfigurer {

    private final MeasurementJwtInterceptor measurementJwtInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(measurementJwtInterceptor)
                .addPathPatterns("/api/monthly/**");
    }
}