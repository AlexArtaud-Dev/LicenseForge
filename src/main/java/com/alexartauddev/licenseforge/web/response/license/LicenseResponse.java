package com.alexartauddev.licenseforge.web.response.license;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Standard API response")
public class LicenseResponse {

    @Schema(description = "Success status", example = "true")
    private boolean success;

    @Schema(description = "Response message", example = "License activated successfully")
    private String message;

    @Schema(description = "Error code (if applicable)", example = "LICENSE_EXPIRED")
    private String errorCode;

    @Schema(description = "Response data (optional)")
    private Object data;

    public LicenseResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
}