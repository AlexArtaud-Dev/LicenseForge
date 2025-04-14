package com.alexartauddev.licenseforge.config;


import com.alexartauddev.licenseforge.application.service_account.service.ServiceAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class ServiceAccountConfig {

    private final ServiceAccountService serviceAccountService;

    @Bean
    public CommandLineRunner initializeServiceAccount() {
        return args -> serviceAccountService.ensureServiceAccountExists();
    }
}