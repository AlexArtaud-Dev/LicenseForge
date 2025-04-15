package com.alexartauddev.licenseforge.application.company.service.impl;

import com.alexartauddev.licenseforge.application.company.service.CompanyOnboardingService;
import com.alexartauddev.licenseforge.application.company.service.CompanyService;
import com.alexartauddev.licenseforge.application.email.service.EmailService;
import com.alexartauddev.licenseforge.application.email.service.EmailTemplateService;
import com.alexartauddev.licenseforge.application.realm.service.RealmService;
import com.alexartauddev.licenseforge.application.user.service.UserService;
import com.alexartauddev.licenseforge.domain.company.entity.Company;
import com.alexartauddev.licenseforge.domain.user.entity.User;
import com.alexartauddev.licenseforge.web.dto.company.CompanyDTO;
import com.alexartauddev.licenseforge.web.dto.realm.RealmDTO;
import com.alexartauddev.licenseforge.web.dto.user.UserDTO;
import com.alexartauddev.licenseforge.web.request.company.CreateCompanyRequest;
import com.alexartauddev.licenseforge.web.request.realm.CreateRealmRequest;
import com.alexartauddev.licenseforge.web.request.user.CreateUserRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CompanyOnboardingServiceImpl implements CompanyOnboardingService {

    private final CompanyService companyService;
    private final RealmService realmService;
    private final UserService userService;
    private final EmailTemplateService emailTemplateService;
    private final EmailService emailService;

    @Override
    @Transactional
    public CompanyDTO createCompanyWithAdmin(String companyName, String adminEmail) {
        log.info("Creating new company: {}, admin: {}", companyName, adminEmail);

        // 1. Generate a unique realm ID based on company name
        String realmId = generateRealmId(companyName);
        log.info("Generated realm ID: {}", realmId);

        // 2. Create the company
        CreateCompanyRequest companyRequest = CreateCompanyRequest.builder()
                .name(companyName) // Use .name instead of .companyName
                .realmId(realmId)
                .quotaApps(3) // Default for free tier
                .quotaKeysPerApp(10) // Default for free tier
                .planType(Company.PlanType.FREE)
                .build();

        CompanyDTO company = companyService.createCompany(companyRequest);
        log.info("Company created with ID: {}", company.getId());

        // 3. Create a default realm for the company
        CreateRealmRequest realmRequest = CreateRealmRequest.builder()
                .name("Default")
                .description("Default realm for " + companyName)
                .companyId(company.getId())
                .build();

        RealmDTO realm = realmService.createRealm(realmRequest);
        log.info("Default realm created with ID: {}", realm.getId());

        // 4. Generate a secure random password
        String generatedPassword = generateSecurePassword();

        // 5. Create admin user
        String adminFirstName = "Admin";
        String adminLastName = companyName;

        CreateUserRequest userRequest = CreateUserRequest.builder()
                .email(adminEmail)
                .firstName(adminFirstName)
                .lastName(adminLastName)
                .password(generatedPassword)
                .role(User.Role.ADMIN)
                .companyId(company.getId())
                .build();

        UserDTO adminUser = userService.createUser(userRequest);
        log.info("Admin user created with ID: {}", adminUser.getId());

        // 6. Log the credentials (would be emailed in production)
//        logAdminCredentials(adminEmail, generatedPassword, companyName);
        String emailContent = emailTemplateService.generateCompanyAdminWelcomeEmail(adminEmail, adminFirstName + "" + adminLastName, generatedPassword, companyName);
        emailService.sendHtmlMessage(adminEmail, "Welcome to LicenseForge - Your Admin Account", emailContent);

        return company;
    }

    private String generateRealmId(String companyName) {
        // Convert company name to lowercase, replace spaces with hyphens, and remove special characters
        String baseRealmId = companyName.toLowerCase()
                .replaceAll("\\s+", "-")
                .replaceAll("[^a-z0-9-]", "");

        // Add random suffix to ensure uniqueness
        String randomSuffix = RandomStringUtils.randomNumeric(4);
        return baseRealmId + "-" + randomSuffix;
    }

    private String generateSecurePassword() {
        // Generate a password with 8 letters, 2 numbers, and 2 special characters
        String letters = RandomStringUtils.randomAlphabetic(8);
        String numbers = RandomStringUtils.randomNumeric(2);
        String specialChars = RandomStringUtils.random(2, "!@#$%^&*()");

        return letters + numbers + specialChars;
    }

    private void logAdminCredentials(String email, String password, String companyName) {
        log.info("======================================================");
        log.info("New company admin credentials (would be emailed to {})", email);
        log.info("Company: {}", companyName);
        log.info("Email: {}", email);
        log.info("Password: {}", password);
        log.info("======================================================");
    }
}