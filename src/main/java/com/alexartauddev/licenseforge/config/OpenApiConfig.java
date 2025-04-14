package com.alexartauddev.licenseforge.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.customizers.OpenApiCustomizer;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI licenseForgeOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("LicenseForge API")
                        .description("Modern Open Source Licensing SaaS")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("Alex Artaud")
                                .url("https://github.com/AlexArtaud-Dev"))
                        .license(new License()
                                .name("MIT")
                                .url("https://opensource.org/licenses/MIT")))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth",
                                new SecurityScheme()
                                        .name("bearerAuth")
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Enter JWT token with Bearer prefix, e.g., 'Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...'")));
    }

    @Bean
    public OpenApiCustomizer openApiCustomizer() {
        return openApi ->
                openApi.servers(List.of(
                        new Server().url("http://localhost:8080").description("Local server")));
    }
}