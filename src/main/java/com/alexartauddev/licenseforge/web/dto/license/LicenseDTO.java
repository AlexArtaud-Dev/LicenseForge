package com.alexartauddev.licenseforge.web.dto.license;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "License information")
public class LicenseDTO {

    @Schema(description = "License ID", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID id;

    @Schema(description = "License key", example = "LFORG-1234-5678-9ABC-DEFG")
    @NotBlank(message = "License key cannot be blank")
    private String licenseKey;

    @Schema(description = "Product ID", example = "product-1")
    @NotBlank(message = "Product ID cannot be blank")
    private String productId;

    @Schema(description = "Customer ID", example = "customer-1")
    @NotBlank(message = "Customer ID cannot be blank")
    private String customerId;

    @Schema(description = "Creation date")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @Schema(description = "Expiration date")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime expiresAt;

    @Schema(description = "Maximum allowed activations", example = "5")
    @NotNull(message = "Max activations cannot be null")
    @Min(value = 1, message = "Max activations must be at least 1")
    private Integer maxActivations;

    @Schema(description = "Whether the license is revoked", example = "false")
    private boolean revoked;

    @Schema(description = "Hardware IDs that have activated this license")
    private Set<String> hardwareIds;
}