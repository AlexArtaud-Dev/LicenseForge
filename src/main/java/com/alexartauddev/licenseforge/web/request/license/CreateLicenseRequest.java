package com.alexartauddev.licenseforge.web.request.license;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateLicenseRequest {
    @NotNull(message = "Application ID is required")
    private UUID appId;

    @NotBlank(message = "Customer ID is required")
    private String customerId;

    private LocalDateTime expiresAt;

    @Min(value = 1, message = "Maximum activations must be at least 1")
    private int maxActivations;
}