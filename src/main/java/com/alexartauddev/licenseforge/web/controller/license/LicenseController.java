package com.alexartauddev.licenseforge.web.controller.license;

import com.alexartauddev.licenseforge.application.license.service.LicenseService;
import com.alexartauddev.licenseforge.web.dto.license.ActivationDTO;
import com.alexartauddev.licenseforge.web.dto.license.LicenseDTO;
import com.alexartauddev.licenseforge.web.request.license.ActivateLicenseRequest;
import com.alexartauddev.licenseforge.web.request.license.CreateLicenseRequest;
import com.alexartauddev.licenseforge.web.request.license.UpdateLicenseRequest;
import com.alexartauddev.licenseforge.web.response.license.ActivationResponse;
import com.alexartauddev.licenseforge.web.response.license.LicenseListResponse;
import com.alexartauddev.licenseforge.web.response.license.LicenseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/licenses")
@RequiredArgsConstructor
@Tag(name = "Licenses", description = "License management API")
public class LicenseController {

    private final LicenseService licenseService;

    @PostMapping
    @Operation(summary = "Create a new license")
    public ResponseEntity<LicenseResponse> createLicense(@Valid @RequestBody CreateLicenseRequest request) {
        LicenseDTO license = licenseService.createLicense(request);
        return new ResponseEntity<>(new LicenseResponse(license), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get license by ID")
    public ResponseEntity<LicenseResponse> getLicenseById(
            @Parameter(description = "License ID", required = true)
            @PathVariable UUID id) {
        LicenseDTO license = licenseService.getLicenseById(id);
        return ResponseEntity.ok(new LicenseResponse(license));
    }

    @GetMapping("/key/{licenseKey}")
    @Operation(summary = "Get license by key")
    public ResponseEntity<LicenseResponse> getLicenseByKey(
            @Parameter(description = "License key", required = true)
            @PathVariable String licenseKey) {
        LicenseDTO license = licenseService.getLicenseByKey(licenseKey);
        return ResponseEntity.ok(new LicenseResponse(license));
    }

    @GetMapping("/app/{appId}")
    @Operation(summary = "Get licenses by application ID")
    public ResponseEntity<LicenseListResponse> getLicensesByAppId(
            @Parameter(description = "Application ID", required = true)
            @PathVariable UUID appId,
            @Parameter(description = "Page number (0-based)")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size")
            @RequestParam(defaultValue = "20") int size) {
        List<LicenseDTO> licenses = licenseService.getLicensesByAppId(appId, page, size);
        long total = licenseService.countActiveLicensesByAppId(appId);
        return ResponseEntity.ok(new LicenseListResponse(licenses, total, page, size));
    }

    @GetMapping("/customer/{customerId}")
    @Operation(summary = "Get licenses by customer ID")
    public ResponseEntity<LicenseListResponse> getLicensesByCustomerId(
            @Parameter(description = "Customer ID", required = true)
            @PathVariable String customerId,
            @Parameter(description = "Page number (0-based)")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size")
            @RequestParam(defaultValue = "20") int size) {
        List<LicenseDTO> licenses = licenseService.getLicensesByCustomerId(customerId, page, size);
        return ResponseEntity.ok(new LicenseListResponse(licenses, licenses.size(), page, size));
    }

    @GetMapping("/app/{appId}/active")
    @Operation(summary = "Get active licenses by application ID")
    public ResponseEntity<LicenseListResponse> getActiveLicensesByAppId(
            @Parameter(description = "Application ID", required = true)
            @PathVariable UUID appId,
            @Parameter(description = "Page number (0-based)")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size")
            @RequestParam(defaultValue = "20") int size) {
        List<LicenseDTO> licenses = licenseService.getActiveLicensesByAppId(appId, page, size);
        long total = licenseService.countActiveLicensesByAppId(appId);
        return ResponseEntity.ok(new LicenseListResponse(licenses, total, page, size));
    }

    @GetMapping("/app/{appId}/expiring")
    @Operation(summary = "Get expiring licenses by application ID within a date range")
    public ResponseEntity<LicenseListResponse> getExpiringLicenses(
            @Parameter(description = "Application ID", required = true)
            @PathVariable UUID appId,
            @Parameter(description = "Start date (ISO format)")
            @RequestParam LocalDateTime start,
            @Parameter(description = "End date (ISO format)")
            @RequestParam LocalDateTime end,
            @Parameter(description = "Page number (0-based)")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size")
            @RequestParam(defaultValue = "20") int size) {
        List<LicenseDTO> licenses = licenseService.getExpiringLicenses(appId, start, end, page, size);
        return ResponseEntity.ok(new LicenseListResponse(licenses, licenses.size(), page, size));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a license")
    public ResponseEntity<LicenseResponse> updateLicense(
            @Parameter(description = "License ID", required = true)
            @PathVariable UUID id,
            @Valid @RequestBody UpdateLicenseRequest request) {
        LicenseDTO license = licenseService.updateLicense(id, request);
        return ResponseEntity.ok(new LicenseResponse(license));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a license")
    public ResponseEntity<Void> deleteLicense(
            @Parameter(description = "License ID", required = true)
            @PathVariable UUID id) {
        licenseService.deleteLicense(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/revoke")
    @Operation(summary = "Revoke a license")
    public ResponseEntity<LicenseResponse> revokeLicense(
            @Parameter(description = "License ID", required = true)
            @PathVariable UUID id) {
        LicenseDTO license = licenseService.revokeLicense(id);
        return ResponseEntity.ok(new LicenseResponse(license));
    }

    @PostMapping("/{licenseKey}/activate")
    @Operation(summary = "Activate a license with a hardware ID")
    public ResponseEntity<ActivationResponse> activateLicense(
            @Parameter(description = "License key", required = true)
            @PathVariable String licenseKey,
            @Valid @RequestBody ActivateLicenseRequest request) {
        ActivationDTO activation = licenseService.activateLicense(licenseKey, request.getHardwareId());
        return new ResponseEntity<>(new ActivationResponse(activation), HttpStatus.CREATED);
    }

    @DeleteMapping("/{licenseKey}/deactivate")
    @Operation(summary = "Deactivate a license for a hardware ID")
    public ResponseEntity<Void> deactivateLicense(
            @Parameter(description = "License key", required = true)
            @PathVariable String licenseKey,
            @Parameter(description = "Hardware ID", required = true)
            @RequestParam String hardwareId) {
        boolean success = licenseService.deactivateLicense(licenseKey, hardwareId);
        return success ? ResponseEntity.noContent().build() : ResponseEntity.badRequest().build();
    }

    @GetMapping("/{licenseKey}/validate")
    @Operation(summary = "Validate a license for a hardware ID")
    public ResponseEntity<Void> validateLicense(
            @Parameter(description = "License key", required = true)
            @PathVariable String licenseKey,
            @Parameter(description = "Hardware ID", required = true)
            @RequestParam String hardwareId) {
        boolean valid = licenseService.validateLicense(licenseKey, hardwareId);
        return valid ? ResponseEntity.ok().build() : ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @GetMapping("/app/{appId}/count")
    @Operation(summary = "Count active licenses by application ID")
    public ResponseEntity<Long> countActiveLicensesByAppId(
            @Parameter(description = "Application ID", required = true)
            @PathVariable UUID appId) {
        long count = licenseService.countActiveLicensesByAppId(appId);
        return ResponseEntity.ok(count);
    }
}