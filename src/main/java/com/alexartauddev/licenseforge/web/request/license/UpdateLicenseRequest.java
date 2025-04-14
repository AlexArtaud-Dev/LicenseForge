package com.alexartauddev.licenseforge.web.request.license;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateLicenseRequest {
    private String customerId;
    private LocalDateTime expiresAt;

    @Min(value = 1, message = "Maximum activations must be at least 1")
    private Integer maxActivations;

    private Boolean revoked;
}