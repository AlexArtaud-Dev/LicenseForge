package com.alexartauddev.licenseforge.web.request.license;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "License creation request")
public class CreateLicenseRequest {

    @Schema(description = "Product ID", example = "product-1")
    @NotBlank(message = "Product ID cannot be blank")
    private String productId;

    @Schema(description = "Customer ID", example = "customer-1")
    @NotBlank(message = "Customer ID cannot be blank")
    private String customerId;

    @Schema(description = "Maximum allowed activations", example = "5")
    @Min(value = 1, message = "Max activations must be at least 1")
    private int maxActivations;

    @Schema(description = "Expiration date (optional)")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDateTime expiresAt;
}