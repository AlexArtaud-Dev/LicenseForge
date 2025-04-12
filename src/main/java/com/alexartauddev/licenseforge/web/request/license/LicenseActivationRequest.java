package com.alexartauddev.licenseforge.web.request.license;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "License activation/deactivation request")
public class LicenseActivationRequest {

    @Schema(description = "License key", example = "LFORG-1234-5678-9ABC-DEFG")
    @NotBlank(message = "License key cannot be blank")
    private String licenseKey;

    @Schema(description = "Hardware ID", example = "HWID-1234-5678-9ABC")
    @NotBlank(message = "Hardware ID cannot be blank")
    private String hardwareId;
}