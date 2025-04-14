// Path: src/main/java/com/alexartauddev/licenseforge/domain/application/repository/ApplicationRepository.java
package com.alexartauddev.licenseforge.domain.application.repository;

import com.alexartauddev.licenseforge.domain.application.entity.Application;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for Application entities
 * This is a technology-agnostic interface in the domain layer
 */
public interface ApplicationRepository {

    /**
     * Save an application
     */
    Application save(Application application);

    /**
     * Find an application by its ID
     */
    Optional<Application> findById(UUID id);

    /**
     * Find all applications
     */
    List<Application> findAll();

    /**
     * Find all applications that belong to a specific realm
     */
    List<Application> findByRealmId(UUID realmId);

    /**
     * Find an application by its name and realm ID
     */
    Optional<Application> findByNameAndRealmId(String name, UUID realmId);

    /**
     * Count applications by realm ID
     */
    long countByRealmId(UUID realmId);

    /**
     * Delete an application
     */
    void delete(Application application);
}