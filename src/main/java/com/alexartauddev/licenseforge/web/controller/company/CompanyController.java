package com.alexartauddev.licenseforge.web.controller.company;

import com.alexartauddev.licenseforge.application.company.service.CompanyService;
import com.alexartauddev.licenseforge.domain.company.entity.Company;
import com.alexartauddev.licenseforge.web.dto.company.CompanyDTO;
import com.alexartauddev.licenseforge.web.request.company.CreateCompanyRequest;
import com.alexartauddev.licenseforge.web.request.company.UpdateCompanyRequest;
import com.alexartauddev.licenseforge.web.response.company.CompanyListResponse;
import com.alexartauddev.licenseforge.web.response.company.CompanyResponse;
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
@RequestMapping("/api/v1/companies")
@RequiredArgsConstructor
@Tag(name = "Companies", description = "Company management API")
public class CompanyController {

    private final CompanyService companyService;

    @PostMapping
    @Operation(summary = "Create a new company")
    public ResponseEntity<CompanyResponse> createCompany(@Valid @RequestBody CreateCompanyRequest request) {
        CompanyDTO company = companyService.createCompany(request);
        return new ResponseEntity<>(new CompanyResponse(company), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get company by ID")
    public ResponseEntity<CompanyResponse> getCompanyById(
            @Parameter(description = "Company ID", required = true)
            @PathVariable UUID id) {
        CompanyDTO company = companyService.getCompanyById(id);
        return ResponseEntity.ok(new CompanyResponse(company));
    }

    @GetMapping("/realm/{realmId}")
    @Operation(summary = "Get company by realm ID")
    public ResponseEntity<CompanyResponse> getCompanyByRealmId(
            @Parameter(description = "Realm ID", required = true)
            @PathVariable String realmId) {
        CompanyDTO company = companyService.getCompanyByRealmId(realmId);
        return ResponseEntity.ok(new CompanyResponse(company));
    }

    @GetMapping
    @Operation(summary = "Get all companies")
    public ResponseEntity<CompanyListResponse> getAllCompanies(
            @Parameter(description = "Page number (0-based)")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size")
            @RequestParam(defaultValue = "20") int size) {
        List<CompanyDTO> companies = companyService.getAllCompanies(page, size);
        long total = companyService.countCompanies();
        return ResponseEntity.ok(new CompanyListResponse(companies, total, page, size));
    }

    @GetMapping("/plan/{planType}")
    @Operation(summary = "Get companies by plan type")
    public ResponseEntity<CompanyListResponse> getCompaniesByPlanType(
            @Parameter(description = "Plan type", required = true)
            @PathVariable Company.PlanType planType,
            @Parameter(description = "Page number (0-based)")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size")
            @RequestParam(defaultValue = "20") int size) {
        List<CompanyDTO> companies = companyService.getCompaniesByPlanType(planType, page, size);
        long total = companyService.countByPlanType(planType);
        return ResponseEntity.ok(new CompanyListResponse(companies, total, page, size));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a company")
    public ResponseEntity<CompanyResponse> updateCompany(
            @Parameter(description = "Company ID", required = true)
            @PathVariable UUID id,
            @Valid @RequestBody UpdateCompanyRequest request) {
        CompanyDTO company = companyService.updateCompany(id, request);
        return ResponseEntity.ok(new CompanyResponse(company));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a company")
    public ResponseEntity<Void> deleteCompany(
            @Parameter(description = "Company ID", required = true)
            @PathVariable UUID id) {
        companyService.deleteCompany(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/count")
    @Operation(summary = "Count all companies")
    public ResponseEntity<Long> countCompanies() {
        long count = companyService.countCompanies();
        return ResponseEntity.ok(count);
    }

    @GetMapping("/plan/{planType}/count")
    @Operation(summary = "Count companies by plan type")
    public ResponseEntity<Long> countByPlanType(
            @Parameter(description = "Plan type", required = true)
            @PathVariable Company.PlanType planType) {
        long count = companyService.countByPlanType(planType);
        return ResponseEntity.ok(count);
    }
}