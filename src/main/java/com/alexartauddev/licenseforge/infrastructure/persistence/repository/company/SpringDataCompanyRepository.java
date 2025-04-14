package com.alexartauddev.licenseforge.infrastructure.persistence.repository.company;

import com.alexartauddev.licenseforge.domain.company.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data JPA repository for Company entities
 */
@Repository
interface SpringDataCompanyRepository extends JpaRepository<Company, UUID> {

    Optional<Company> findByRealmId(String realmId);

    Optional<Company> findByName(String name);

    boolean existsByRealmId(String realmId);

    List<Company> findByPlanType(Company.PlanType planType);
}