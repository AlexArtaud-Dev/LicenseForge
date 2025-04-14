package com.alexartauddev.licenseforge.application.company.service;

import com.alexartauddev.licenseforge.domain.company.entity.Company;
import com.alexartauddev.licenseforge.web.dto.company.CompanyDTO;
import com.alexartauddev.licenseforge.web.request.company.CreateCompanyRequest;
import com.alexartauddev.licenseforge.web.request.company.UpdateCompanyRequest;

import java.util.List;
import java.util.UUID;

public interface CompanyService {
    CompanyDTO createCompany(CreateCompanyRequest request);

    CompanyDTO getCompanyById(UUID id);

    CompanyDTO getCompanyByRealmId(String realmId);

    List<CompanyDTO> getAllCompanies(int page, int size);

    List<CompanyDTO> getCompaniesByPlanType(Company.PlanType planType, int page, int size);

    CompanyDTO updateCompany(UUID id, UpdateCompanyRequest request);

    void deleteCompany(UUID id);

    long countCompanies();

    long countByPlanType(Company.PlanType planType);
}