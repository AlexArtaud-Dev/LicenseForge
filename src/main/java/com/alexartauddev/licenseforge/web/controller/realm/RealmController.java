package com.alexartauddev.licenseforge.web.controller.realm;

import com.alexartauddev.licenseforge.application.realm.service.RealmService;
import com.alexartauddev.licenseforge.web.dto.realm.RealmDTO;
import com.alexartauddev.licenseforge.web.request.realm.CreateRealmRequest;
import com.alexartauddev.licenseforge.web.request.realm.UpdateRealmRequest;
import com.alexartauddev.licenseforge.web.response.realm.RealmListResponse;
import com.alexartauddev.licenseforge.web.response.realm.RealmResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/realms")
@RequiredArgsConstructor
@Tag(name = "Realms", description = "Realm management API")
public class RealmController {

    private final RealmService realmService;

    @PostMapping
    @Operation(summary = "Create a new realm")
    public ResponseEntity<RealmResponse> createRealm(@Valid @RequestBody CreateRealmRequest request) {
        RealmDTO realm = realmService.createRealm(request);
        return new ResponseEntity<>(new RealmResponse(realm), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get realm by ID")
    public ResponseEntity<RealmResponse> getRealmById(
            @Parameter(description = "Realm ID", required = true)
            @PathVariable UUID id) {
        RealmDTO realm = realmService.getRealmById(id);
        return ResponseEntity.ok(new RealmResponse(realm));
    }

    @GetMapping("/company/{companyId}")
    @Operation(summary = "Get realms by company ID")
    public ResponseEntity<RealmListResponse> getRealmsByCompanyId(
            @Parameter(description = "Company ID", required = true)
            @PathVariable UUID companyId,
            @Parameter(description = "Page number (0-based)")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size")
            @RequestParam(defaultValue = "20") int size) {
        List<RealmDTO> realms = realmService.getRealmsByCompanyId(companyId, page, size);
        long total = realmService.countByCompanyId(companyId);
        return ResponseEntity.ok(new RealmListResponse(realms, total, page, size));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a realm")
    public ResponseEntity<RealmResponse> updateRealm(
            @Parameter(description = "Realm ID", required = true)
            @PathVariable UUID id,
            @Valid @RequestBody UpdateRealmRequest request) {
        RealmDTO realm = realmService.updateRealm(id, request);
        return ResponseEntity.ok(new RealmResponse(realm));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a realm")
    public ResponseEntity<Void> deleteRealm(
            @Parameter(description = "Realm ID", required = true)
            @PathVariable UUID id) {
        realmService.deleteRealm(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/company/{companyId}/count")
    @Operation(summary = "Count realms by company ID")
    public ResponseEntity<Long> countByCompanyId(
            @Parameter(description = "Company ID", required = true)
            @PathVariable UUID companyId) {
        long count = realmService.countByCompanyId(companyId);
        return ResponseEntity.ok(count);
    }
}
