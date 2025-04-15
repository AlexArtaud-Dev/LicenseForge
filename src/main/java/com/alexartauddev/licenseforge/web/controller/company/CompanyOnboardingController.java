package com.alexartauddev.licenseforge.web.controller.company;

import com.alexartauddev.licenseforge.application.company.service.CompanyOnboardingService;
import com.alexartauddev.licenseforge.web.dto.company.CompanyDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/onboarding")
@RequiredArgsConstructor
@Tag(name = "Company Onboarding", description = "API for company registration and onboarding")
public class CompanyOnboardingController {

    private final CompanyOnboardingService companyOnboardingService;

    @Data
    public static class OnboardingRequest {
        @NotBlank(message = "Company name is required")
        private String companyName;

        @NotBlank(message = "Admin email is required")
        @Email(message = "Invalid email format")
        private String adminEmail;
    }

    @Data
    public static class OnboardingResponse {
        private CompanyDTO company;
        private String message;
    }

    @PostMapping("/company")
    @Operation(summary = "Register a new company",
            description = "Creates a new company with a default realm and admin user. Admin credentials are sent to the provided email address.")
    public ResponseEntity<OnboardingResponse> createCompany(
            @Parameter(description = "Company creation details", required = true)
            @Valid @RequestBody OnboardingRequest request) {

        CompanyDTO company = companyOnboardingService.createCompanyWithAdmin(
                request.getCompanyName(),
                request.getAdminEmail()
        );

        OnboardingResponse response = new OnboardingResponse();
        response.setCompany(company);
        response.setMessage("Company created successfully. Admin credentials have been sent to " + request.getAdminEmail());

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}