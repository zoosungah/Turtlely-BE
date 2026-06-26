package com.project.turtlely.domain.measurement.jwt;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MeasurementSwaggerConfig {

    @Bean
    public OpenApiCustomizer measurementApiCustomizer() {
        return openApi -> {
            String jwtSchemeName = "JWT_TOKEN";

            SecurityRequirement requirement = new SecurityRequirement().addList(jwtSchemeName);
            if (openApi.getSecurity() == null) {
                openApi.addSecurityItem(requirement);
            } else if (openApi.getSecurity().stream().noneMatch(s -> s.containsKey(jwtSchemeName))) {
                openApi.addSecurityItem(requirement);
            }

            if (openApi.getComponents() == null) {
                openApi.setComponents(new Components());
            }

            if (openApi.getComponents().getSecuritySchemes() == null ||
                    !openApi.getComponents().getSecuritySchemes().containsKey(jwtSchemeName)) {

                openApi.getComponents().addSecuritySchemes(jwtSchemeName, new SecurityScheme()
                        .name(jwtSchemeName)
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT"));
            }
        };
    }
}