package com.alexartauddev.licenseforge.web.response.license;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "License verification response")
public class VerifyLicenseResponse {

    @Schema(description = "Success status", example = "true")
    private boolean success;

    @Schema(description = "Response message", example = "License is valid and can be activated")
    private String message;

    @Schema(description = "Error code (if error occurred)",
            example = "LICENSE_EXPIRED",
            allowableValues = {"LICENSE_NOT_FOUND", "LICENSE_EXPIRED", "LICENSE_REVOKED", "MAX_ACTIVATIONS_REACHED"})
    private String errorCode;

    @Schema(description = "Verification status (if success=true)",
            example = "AVAILABLE_FOR_ACTIVATION",
            allowableValues = {"ALREADY_ACTIVATED", "AVAILABLE_FOR_ACTIVATION"})
    private String status;

    @Schema(description = "Current number of activations", example = "2")
    private Integer activationCount;

    @Schema(description = "Maximum allowed activations", example = "5")
    private Integer maxActivations;

    @Schema(description = "License expiration date", example = "2025-12-31T23:59:59")
    private LocalDateTime expiresAt;
}