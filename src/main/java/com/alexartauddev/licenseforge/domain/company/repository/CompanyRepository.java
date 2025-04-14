// Path: src/main/java/com/alexartauddev/licenseforge/domain/company/repository/CompanyRepository.java
package com.alexartauddev.licenseforge.domain.company.repository;

import com.alexartauddev.licenseforge.domain.company.entity.Company;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for Company entities
 * This is a technology-agnostic interface in the domain layer
 */
public interface CompanyRepository {

    /**
     * Save a company
     */
    Company save(Company company);

    /**
     * Find a company by its ID
     */
    Optional<Company> findById(UUID id);

    /**
     * Find all companies
     */
    List<Company> findAll();

    /**
     * Find a company by its realm ID
     */
    Optional<Company> findByRealmId(String realmId);

    /**
     * Find a company by its name
     */
    Optional<Company> findByName(String name);

    /**
     * Check if a company exists with the given realm ID
     */
    boolean existsByRealmId(String realmId);

    /**
     * Find all companies of a specific plan type
     */
    List<Company> findByPlanType(Company.PlanType planType);

    /**
     * Delete a company
     */
    void delete(Company company);
}