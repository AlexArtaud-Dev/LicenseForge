package com.alexartauddev.licenseforge.application.license.service;

import com.alexartauddev.licenseforge.web.dto.license.ActivationDTO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface ActivationService {
    /**
     * Get all activations for a license
     */
    List<ActivationDTO> getActivationsByLicenseId(UUID licenseId, int page, int size);

    /**
     * Get a specific activation by ID
     */
    ActivationDTO getActivationById(UUID id);

    /**
     * Find an activation by license ID and hardware ID
     */
    ActivationDTO getActivationByLicenseIdAndHardwareId(UUID licenseId, String hardwareId);

    /**
     * Update the last seen timestamp for an activation
     */
    ActivationDTO updateLastSeen(UUID id);

    /**
     * Find inactive activations (not seen since a threshold time)
     */
    List<ActivationDTO> findInactiveActivations(LocalDateTime threshold, int page, int size);

    /**
     * Delete an activation
     */
    void deleteActivation(UUID id);

    /**
     * Count activations for a license
     */
    long countByLicenseId(UUID licenseId);
}