package com.alexartauddev.licenseforge.web.controller;

import com.alexartauddev.licenseforge.application.service.LicenseService;
import com.alexartauddev.licenseforge.web.request.CreateLicenseRequest;
import com.alexartauddev.licenseforge.web.request.LicenseActivationRequest;
import com.alexartauddev.licenseforge.web.dto.license.LicenseDTO;
import com.alexartauddev.licenseforge.web.response.LicenseResponse;
import com.alexartauddev.licenseforge.web.response.VerifyLicenseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/licenses")
@Tag(name = "License Management", description = "APIs for managing software licenses")
public class LicenseController {

    private final LicenseService licenseService;

    @Autowired
    public LicenseController(LicenseService licenseService) {
        this.licenseService = licenseService;
    }

    @PostMapping
    @Operation(summary = "Create a new license", description = "Creates a new license with the given parameters")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "License created successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = LicenseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = LicenseResponse.class)))
    })
    public ResponseEntity<LicenseDTO> createLicense(@Valid @RequestBody CreateLicenseRequest request) {
        LicenseDTO license = licenseService.createLicense(
                request.getProductId(),
                request.getCustomerId(),
                request.getMaxActivations(),
                request.getExpiresAt()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(license);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get license by ID", description = "Retrieves a license by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "License found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = LicenseDTO.class))),
            @ApiResponse(responseCode = "404", description = "License not found")
    })
    public ResponseEntity<LicenseDTO> getLicense(
            @Parameter(description = "License ID", required = true) @PathVariable UUID id) {
        return licenseService.getLicense(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/key/{licenseKey}")
    @Operation(summary = "Get license by key", description = "Retrieves a license by its key")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "License found"),
            @ApiResponse(responseCode = "404", description = "License not found")
    })
    public ResponseEntity<LicenseDTO> getLicenseByKey(
            @Parameter(description = "License key", required = true) @PathVariable String licenseKey) {
        return licenseService.getLicenseByKey(licenseKey)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/customer/{customerId}")
    @Operation(summary = "Get licenses by customer", description = "Retrieves all licenses for a customer")
    public ResponseEntity<List<LicenseDTO>> getLicensesByCustomer(
            @Parameter(description = "Customer ID", required = true) @PathVariable String customerId) {
        List<LicenseDTO> licenses = licenseService.getLicensesByCustomer(customerId);
        return ResponseEntity.ok(licenses);
    }

    @GetMapping("/product/{productId}")
    @Operation(summary = "Get licenses by product", description = "Retrieves all licenses for a product")
    public ResponseEntity<List<LicenseDTO>> getLicensesByProduct(
            @Parameter(description = "Product ID", required = true) @PathVariable String productId) {
        List<LicenseDTO> licenses = licenseService.getLicensesByProduct(productId);
        return ResponseEntity.ok(licenses);
    }


    @PostMapping("/verify")
    @Operation(
            summary = "Verify license",
            description = "Verifies if a license key is valid for a given hardware ID and returns detailed status information"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Verification completed",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = VerifyLicenseResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = VerifyLicenseResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = VerifyLicenseResponse.class))
            )
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "License key and hardware ID to verify",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = LicenseActivationRequest.class)
            ),
            required = true
    )
    public ResponseEntity<VerifyLicenseResponse> verifyLicense(@Valid @RequestBody LicenseActivationRequest request) {
        Map<String, Object> result = licenseService.verifyLicense(request.getLicenseKey(), request.getHardwareId());
        boolean isValid = (boolean) result.get("success");

        VerifyLicenseResponse.VerifyLicenseResponseBuilder responseBuilder = VerifyLicenseResponse.builder()
                .success(isValid)
                .message((String) result.get("message"));

        if (isValid) {
            // Add status
            responseBuilder.status((String) result.get("status"));

            // Add activation counts
            if (result.containsKey("activationCount")) {
                responseBuilder.activationCount((Integer) result.get("activationCount"));
                responseBuilder.maxActivations((Integer) result.get("maxActivations"));
            }

            // Add expiration date if present
            if (result.containsKey("expiresAt")) {
                responseBuilder.expiresAt((LocalDateTime) result.get("expiresAt"));
            }
        } else {
            // Add error code
            responseBuilder.errorCode((String) result.get("errorCode"));

            // Add activation counts if present
            if (result.containsKey("activationCount")) {
                responseBuilder.activationCount((Integer) result.get("activationCount"));
                responseBuilder.maxActivations((Integer) result.get("maxActivations"));
            }

            // Add expiry date if present
            if (result.containsKey("expiryDate")) {
                responseBuilder.expiresAt((LocalDateTime) result.get("expiryDate"));
            }
        }

        return ResponseEntity.ok(responseBuilder.build());
    }

    @PostMapping("/activate")
    @Operation(
            summary = "Activate license",
            description = "Activates a license for a given hardware ID and returns detailed status information"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Activation completed",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = VerifyLicenseResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = VerifyLicenseResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = VerifyLicenseResponse.class))
            )
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "License key and hardware ID to activate",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = LicenseActivationRequest.class)
            ),
            required = true
    )
    public ResponseEntity<VerifyLicenseResponse> activateLicense(@Valid @RequestBody LicenseActivationRequest request) {
        Map<String, Object> result = licenseService.activateLicense(request.getLicenseKey(), request.getHardwareId());
        boolean activated = (boolean) result.get("success");

        VerifyLicenseResponse.VerifyLicenseResponseBuilder responseBuilder = VerifyLicenseResponse.builder()
                .success(activated)
                .message((String) result.get("message"));

        if (activated) {
            // Add activation counts if present
            if (result.containsKey("activationCount")) {
                responseBuilder.activationCount((Integer) result.get("activationCount"));
                responseBuilder.maxActivations((Integer) result.get("maxActivations"));
            }
        } else {
            // Add error code
            responseBuilder.errorCode((String) result.get("errorCode"));

            // Add activation counts if present
            if (result.containsKey("activationCount")) {
                responseBuilder.activationCount((Integer) result.get("activationCount"));
                responseBuilder.maxActivations((Integer) result.get("maxActivations"));
            }

            // Add expiry date if present
            if (result.containsKey("expiryDate")) {
                responseBuilder.expiresAt((LocalDateTime) result.get("expiryDate"));
            }
        }

        return ResponseEntity.ok(responseBuilder.build());
    }

    @PostMapping("/deactivate")
    @Operation(
            summary = "Deactivate license",
            description = "Deactivates a license for a given hardware ID and returns detailed status information"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Deactivation completed",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = VerifyLicenseResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = VerifyLicenseResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = VerifyLicenseResponse.class))
            )
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "License key and hardware ID to deactivate",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = LicenseActivationRequest.class)
            ),
            required = true
    )
    public ResponseEntity<VerifyLicenseResponse> deactivateLicense(@Valid @RequestBody LicenseActivationRequest request) {
        Map<String, Object> result = licenseService.deactivateLicense(request.getLicenseKey(), request.getHardwareId());
        boolean deactivated = (boolean) result.get("success");

        VerifyLicenseResponse.VerifyLicenseResponseBuilder responseBuilder = VerifyLicenseResponse.builder()
                .success(deactivated)
                .message((String) result.get("message"));

        if (!deactivated) {
            // Add error code
            responseBuilder.errorCode((String) result.get("errorCode"));
        }
        if (result.containsKey("activationCount")) {
            responseBuilder.activationCount((Integer) result.get("activationCount"));
            responseBuilder.maxActivations((Integer) result.get("maxActivations"));
        }

        return ResponseEntity.ok(responseBuilder.build());
    }

    @PutMapping("/{id}/revoke")
    @Operation(summary = "Revoke license", description = "Revokes a license by its ID")
    public ResponseEntity<LicenseResponse> revokeLicense(
            @Parameter(description = "License ID", required = true) @PathVariable UUID id) {
        boolean revoked = licenseService.revokeLicense(id);

        LicenseResponse response = LicenseResponse.builder()
                .success(revoked)
                .message(revoked ? "License revoked successfully" : "Failed to revoke license")
                .build();

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/reinstate")
    @Operation(summary = "Reinstate license", description = "Reinstates a previously revoked license")
    public ResponseEntity<LicenseResponse> reinstateLicense(
            @Parameter(description = "License ID", required = true) @PathVariable UUID id) {
        boolean reinstated = licenseService.reinstateRevokedLicense(id);

        LicenseResponse response = LicenseResponse.builder()
                .success(reinstated)
                .message(reinstated ? "License reinstated successfully" : "Failed to reinstate license")
                .build();

        return ResponseEntity.ok(response);
    }
}