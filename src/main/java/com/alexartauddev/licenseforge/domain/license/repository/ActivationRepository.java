// Path: src/main/java/com/alexartauddev/licenseforge/domain/license/repository/ActivationRepository.java
package com.alexartauddev.licenseforge.domain.license.repository;

import com.alexartauddev.licenseforge.domain.license.entity.Activation;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for Activation entities
 * This is a technology-agnostic interface in the domain layer
 */
public interface ActivationRepository {

    /**
     * Save an activation
     */
    Activation save(Activation activation);

    /**
     * Find an activation by its ID
     */
    Optional<Activation> findById(UUID id);

    /**
     * Find all activations
     */
    List<Activation> findAll();

    /**
     * Find all activations for a specific license
     */
    List<Activation> findByLicenseId(UUID licenseId);

    /**
     * Find an activation by license ID and hardware ID
     */
    Optional<Activation> findByLicenseIdAndHardwareId(UUID licenseId, String hardwareId);

    /**
     * Count activations for a specific license
     */
    long countByLicenseId(UUID licenseId);

    /**
     * Find activations that haven't been seen recently (potentially inactive)
     */
    List<Activation> findInactiveActivations(LocalDateTime threshold);

    /**
     * Delete activations for a specific license
     */
    long deleteByLicenseId(UUID licenseId);

    /**
     * Delete an activation
     */
    void delete(Activation activation);
}