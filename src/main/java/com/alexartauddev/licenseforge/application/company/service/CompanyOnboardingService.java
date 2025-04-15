package com.alexartauddev.licenseforge.application.company.service;

import com.alexartauddev.licenseforge.web.dto.company.CompanyDTO;

/**
 * Service for handling company onboarding process
 */
public interface CompanyOnboardingService {

    /**
     * Creates a new company with default realm and admin user
     *
     * @param companyName The name of the company
     * @param adminEmail The email for the company admin
     * @return The created company DTO
     */
    CompanyDTO createCompanyWithAdmin(String companyName, String adminEmail);
}