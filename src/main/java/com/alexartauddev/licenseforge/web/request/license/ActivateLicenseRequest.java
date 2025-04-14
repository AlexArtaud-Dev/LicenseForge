package com.alexartauddev.licenseforge.web.request.license;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivateLicenseRequest {
    @NotBlank(message = "Hardware ID is required")
    private String hardwareId;
}