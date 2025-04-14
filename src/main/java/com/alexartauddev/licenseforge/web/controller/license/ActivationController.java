package com.alexartauddev.licenseforge.web.controller.license;

import com.alexartauddev.licenseforge.application.license.service.ActivationService;
import com.alexartauddev.licenseforge.web.dto.license.ActivationDTO;
import com.alexartauddev.licenseforge.web.response.license.ActivationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/activations")
@RequiredArgsConstructor
@Tag(name = "Activations", description = "License activation management API")
@SecurityRequirement(name = "bearerAuth")
public class ActivationController {

    private final ActivationService activationService;

    @GetMapping("/license/{licenseId}")
    @Operation(summary = "Get all activations for a license")
    public ResponseEntity<List<ActivationDTO>> getActivationsByLicenseId(
            @Parameter(description = "License ID", required = true)
            @PathVariable UUID licenseId,
            @Parameter(description = "Page number (0-based)")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size")
            @RequestParam(defaultValue = "20") int size) {
        List<ActivationDTO> activations = activationService.getActivationsByLicenseId(licenseId, page, size);
        return ResponseEntity.ok(activations);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get activation by ID")
    public ResponseEntity<ActivationResponse> getActivationById(
            @Parameter(description = "Activation ID", required = true)
            @PathVariable UUID id) {
        ActivationDTO activation = activationService.getActivationById(id);
        return ResponseEntity.ok(new ActivationResponse(activation));
    }

    @GetMapping("/license/{licenseId}/hardware/{hardwareId}")
    @Operation(summary = "Get activation by license ID and hardware ID")
    public ResponseEntity<ActivationResponse> getActivationByLicenseIdAndHardwareId(
            @Parameter(description = "License ID", required = true)
            @PathVariable UUID licenseId,
            @Parameter(description = "Hardware ID", required = true)
            @PathVariable String hardwareId) {
        ActivationDTO activation = activationService.getActivationByLicenseIdAndHardwareId(licenseId, hardwareId);
        return ResponseEntity.ok(new ActivationResponse(activation));
    }

    @PutMapping("/{id}/heartbeat")
    @Operation(summary = "Update the last seen timestamp for an activation")
    public ResponseEntity<ActivationResponse> updateLastSeen(
            @Parameter(description = "Activation ID", required = true)
            @PathVariable UUID id) {
        ActivationDTO activation = activationService.updateLastSeen(id);
        return ResponseEntity.ok(new ActivationResponse(activation));
    }

    @GetMapping("/inactive")
    @Operation(summary = "Find inactive activations")
    public ResponseEntity<List<ActivationDTO>> findInactiveActivations(
            @Parameter(description = "Threshold date time (ISO format)")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime threshold,
            @Parameter(description = "Page number (0-based)")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size")
            @RequestParam(defaultValue = "20") int size) {
        List<ActivationDTO> activations = activationService.findInactiveActivations(threshold, page, size);
        return ResponseEntity.ok(activations);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an activation")
    public ResponseEntity<Void> deleteActivation(
            @Parameter(description = "Activation ID", required = true)
            @PathVariable UUID id) {
        activationService.deleteActivation(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/license/{licenseId}/count")
    @Operation(summary = "Count activations for a license")
    public ResponseEntity<Long> countByLicenseId(
            @Parameter(description = "License ID", required = true)
            @PathVariable UUID licenseId) {
        long count = activationService.countByLicenseId(licenseId);
        return ResponseEntity.ok(count);
    }
}
