// Path: src/main/java/com/alexartauddev/licenseforge/domain/license/repository/LicenseRepository.java
package com.alexartauddev.licenseforge.domain.license.repository;

import com.alexartauddev.licenseforge.domain.license.entity.License;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for License entities
 * This is a technology-agnostic interface in the domain layer
 */
public interface LicenseRepository {

    /**
     * Save a license
     */
    License save(License license);

    /**
     * Find a license by its ID
     */
    Optional<License> findById(UUID id);

    /**
     * Find all licenses
     */
    List<License> findAll();

    /**
     * Find a license by its license key
     */
    Optional<License> findByLicenseKey(String licenseKey);

    /**
     * Find all licenses for a specific application
     */
    List<License> findByAppId(UUID appId);

    /**
     * Find all licenses for a specific customer
     */
    List<License> findByCustomerId(String customerId);

    /**
     * Find all active (not revoked and not expired) licenses for a specific application
     */
    List<License> findActiveByAppId(UUID appId, LocalDateTime now);

    /**
     * Find licenses that are expiring soon for a specific application
     */
    List<License> findExpiringLicenses(UUID appId, LocalDateTime start, LocalDateTime end);

    /**
     * Count the number of active licenses for a specific application
     */
    long countActiveByAppId(UUID appId, LocalDateTime now);

    /**
     * Delete a license
     */
    void delete(License license);
}