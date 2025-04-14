package com.alexartauddev.licenseforge.infrastructure.persistence.repository.company;

import com.alexartauddev.licenseforge.domain.company.entity.Company;
import com.alexartauddev.licenseforge.domain.company.repository.CompanyRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * JPA implementation of the CompanyRepository interface
 */
@Repository
public class JpaCompanyRepository implements CompanyRepository {

    private final SpringDataCompanyRepository repository;

    public JpaCompanyRepository(SpringDataCompanyRepository repository) {
        this.repository = repository;
    }

    @Override
    public Company save(Company company) {
        return repository.save(company);
    }

    @Override
    public Optional<Company> findById(UUID id) {
        return repository.findById(id);
    }

    @Override
    public List<Company> findAll() {
        return repository.findAll();
    }

    @Override
    public Optional<Company> findByRealmId(String realmId) {
        return repository.findByRealmId(realmId);
    }

    @Override
    public Optional<Company> findByName(String name) {
        return repository.findByName(name);
    }

    @Override
    public boolean existsByRealmId(String realmId) {
        return repository.existsByRealmId(realmId);
    }

    @Override
    public List<Company> findByPlanType(Company.PlanType planType) {
        return repository.findByPlanType(planType);
    }

    @Override
    public void delete(Company company) {
        repository.delete(company);
    }
}