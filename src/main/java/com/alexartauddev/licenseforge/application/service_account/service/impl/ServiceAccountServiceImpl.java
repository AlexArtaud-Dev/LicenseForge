package com.alexartauddev.licenseforge.application.service_account.service.impl;

import com.alexartauddev.licenseforge.application.service_account.service.ServiceAccountService;
import com.alexartauddev.licenseforge.domain.company.entity.Company;
import com.alexartauddev.licenseforge.domain.company.repository.CompanyRepository;
import com.alexartauddev.licenseforge.domain.user.entity.User;
import com.alexartauddev.licenseforge.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ServiceAccountServiceImpl implements ServiceAccountService {

    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${licenseforge.service-account.email}")
    private String serviceAccountEmail;

    @Value("${licenseforge.service-account.password}")
    private String serviceAccountPassword;

    @Override
    @Transactional
    public void ensureServiceAccountExists() {
        // Check if service account already exists
        Optional<User> existingUser = userRepository.findByEmail(serviceAccountEmail);
        if (existingUser.isPresent()) {
            log.info("Service account already exists");
            return; // Service account already exists
        }

        log.info("Creating service account with email: {}", serviceAccountEmail);

        // Create system company if it doesn't exist
        Company systemCompany = companyRepository.findByRealmId("system")
                .orElseGet(() -> {
                    log.info("Creating system company");
                    Company company = Company.builder()
                            .name("LicenseForge System")
                            .realmId("system")
                            .quotaApps(Integer.MAX_VALUE)
                            .quotaKeysPerApp(Integer.MAX_VALUE)
                            .planType(Company.PlanType.ENTERPRISE)
                            .createdAt(LocalDateTime.now())
                            .updatedAt(LocalDateTime.now())
                            .build();
                    return companyRepository.save(company);
                });

        // Create service account with Admin role
        User serviceAccount = User.builder()
                .email(serviceAccountEmail)
                .firstName("Service")
                .lastName("Account")
                .passwordHash(passwordEncoder.encode(serviceAccountPassword))
                .role(User.Role.ADMIN)  // Using ADMIN since we don't have SUPER_ADMIN
                .companyId(systemCompany.getId())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        userRepository.save(serviceAccount);
        log.info("Service account created successfully");
    }

    @Override
    public boolean isServiceAccount(String email) {
        return serviceAccountEmail.equals(email);
    }
}