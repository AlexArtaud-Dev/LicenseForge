package com.alexartauddev.licenseforge.web.controller.user;

import com.alexartauddev.licenseforge.application.email.service.EmailService;
import com.alexartauddev.licenseforge.application.email.service.EmailTemplateService;
import com.alexartauddev.licenseforge.application.user.service.UserService;
import com.alexartauddev.licenseforge.web.dto.user.UserDTO;
import com.alexartauddev.licenseforge.web.request.user.CreateUserRequest;
import com.alexartauddev.licenseforge.web.request.user.RegisterUserRequest;
import com.alexartauddev.licenseforge.web.response.user.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users/register")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "User Registration", description = "API for user registration by company admins")
@SecurityRequirement(name = "bearerAuth")
public class UserRegistrationController {

    private final UserService userService;
    private final EmailTemplateService emailTemplateService;
    private final EmailService emailService;

    @PostMapping
    @Operation(summary = "Register a new user in the company",
            description = "Allows company admins to create new users without team assignment. A password will be generated and sent to the user's email.")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> registerUser(
            @Parameter(description = "User details", required = true)
            @Valid @RequestBody RegisterUserRequest request) {

        // Get current authenticated user to determine the company
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDTO currentUser = userService.getUserByEmail(authentication.getName());

        UUID companyId = currentUser.getCompanyId();

        // Generate a secure password
        String generatedPassword = generateSecurePassword();

        // Create the user
        CreateUserRequest createUserRequest = CreateUserRequest.builder()
                .email(request.getEmail())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .password(generatedPassword)
                .role(request.getRole())
                .companyId(companyId)
                // No team assignment by default
                .build();

        UserDTO createdUser = userService.createUser(createUserRequest);
        log.info("User {} created by admin {} in company {}",
                createdUser.getId(), currentUser.getId(), companyId);

        // Log credentials (would be emailed in production)
        logUserCredentials(request.getEmail(), generatedPassword,
                request.getFirstName() + " " + request.getLastName(),
                currentUser.getCompanyName());

        String emailContent = emailTemplateService.generateCompanyNewUserWelcomeEmail(request.getEmail(), request.getFirstName() + " " + request.getLastName(), generatedPassword, currentUser.getCompanyName());
        emailService.sendHtmlMessage(request.getEmail(), "Welcome to LicenseForge - Your account", emailContent);

        return new ResponseEntity<>(new UserResponse(createdUser), HttpStatus.CREATED);
    }

    private String generateSecurePassword() {
        // Generate a password with 8 letters, 2 numbers, and 2 special characters
        String letters = RandomStringUtils.randomAlphabetic(8);
        String numbers = RandomStringUtils.randomNumeric(2);
        String specialChars = RandomStringUtils.random(2, "!@#$%^&*()");

        return letters + numbers + specialChars;
    }

    private void logUserCredentials(String email, String password, String userName, String companyName) {
        log.info("======================================================");
        log.info("New user credentials (would be emailed to {})", email);
        log.info("User: {}", userName);
        log.info("Company: {}", companyName);
        log.info("Email: {}", email);
        log.info("Password: {}", password);
        log.info("======================================================");
    }
}