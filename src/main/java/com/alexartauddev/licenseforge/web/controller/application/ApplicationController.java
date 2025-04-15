package com.alexartauddev.licenseforge.web.controller.application;
import com.alexartauddev.licenseforge.application.application.service.ApplicationService;
import com.alexartauddev.licenseforge.web.dto.application.ApplicationDTO;
import com.alexartauddev.licenseforge.web.request.application.CreateApplicationRequest;
import com.alexartauddev.licenseforge.web.request.application.UpdateApplicationRequest;
import com.alexartauddev.licenseforge.web.response.application.ApplicationListResponse;
import com.alexartauddev.licenseforge.web.response.application.ApplicationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/applications")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
@Tag(name = "Applications", description = "Application management API")
@SecurityRequirement(name = "bearerAuth")
public class ApplicationController {

    private final ApplicationService applicationService;

    @PostMapping
    @Operation(summary = "Create a new application")
    public ResponseEntity<ApplicationResponse> createApplication(@Valid @RequestBody CreateApplicationRequest request) {
        ApplicationDTO application = applicationService.createApplication(request);
        return new ResponseEntity<>(new ApplicationResponse(application), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get application by ID")
    public ResponseEntity<ApplicationResponse> getApplicationById(
            @Parameter(description = "Application ID", required = true)
            @PathVariable UUID id) {
        ApplicationDTO application = applicationService.getApplicationById(id);
        return ResponseEntity.ok(new ApplicationResponse(application));
    }

    @GetMapping("/realm/{realmId}")
    @Operation(summary = "Get applications by realm ID")
    public ResponseEntity<ApplicationListResponse> getApplicationsByRealmId(
            @Parameter(description = "Realm ID", required = true)
            @PathVariable UUID realmId,
            @Parameter(description = "Page number (0-based)")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size")
            @RequestParam(defaultValue = "20") int size) {
        List<ApplicationDTO> applications = applicationService.getApplicationsByRealmId(realmId, page, size);
        long total = applicationService.countByRealmId(realmId);
        return ResponseEntity.ok(new ApplicationListResponse(applications, total, page, size));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an application")
    public ResponseEntity<ApplicationResponse> updateApplication(
            @Parameter(description = "Application ID", required = true)
            @PathVariable UUID id,
            @Valid @RequestBody UpdateApplicationRequest request) {
        ApplicationDTO application = applicationService.updateApplication(id, request);
        return ResponseEntity.ok(new ApplicationResponse(application));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an application")
    public ResponseEntity<Void> deleteApplication(
            @Parameter(description = "Application ID", required = true)
            @PathVariable UUID id) {
        applicationService.deleteApplication(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/realm/{realmId}/count")
    @Operation(summary = "Count applications by realm ID")
    public ResponseEntity<Long> countByRealmId(
            @Parameter(description = "Realm ID", required = true)
            @PathVariable UUID realmId) {
        long count = applicationService.countByRealmId(realmId);
        return ResponseEntity.ok(count);
    }
}
