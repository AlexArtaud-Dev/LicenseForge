package com.alexartauddev.licenseforge.infrastructure.persistence.repository.application;

import com.alexartauddev.licenseforge.domain.application.entity.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data JPA repository for Application entities
 * This is the actual JPA implementation that stays in the infrastructure layer
 */
@Repository
interface SpringDataApplicationRepository extends JpaRepository<Application, UUID> {

    List<Application> findByRealmId(UUID realmId);

    Optional<Application> findByNameAndRealmId(String name, UUID realmId);

    long countByRealmId(UUID realmId);
}