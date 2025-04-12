package com.alexartauddev.licenseforge.application.service;

import com.alexartauddev.licenseforge.web.dto.license.LicenseDTO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public interface LicenseService {

    /**
     * Create a new license
     */
    LicenseDTO createLicense(String productId, String customerId, int maxActivations, LocalDateTime expiresAt);

    /**
     * Get license by ID
     */
    Optional<LicenseDTO> getLicense(UUID id);

    /**
     * Get license by key
     */
    Optional<LicenseDTO> getLicenseByKey(String licenseKey);

    /**
     * Get all licenses for a customer
     */
    List<LicenseDTO> getLicensesByCustomer(String customerId);

    /**
     * Get all licenses for a product
     */
    List<LicenseDTO> getLicensesByProduct(String productId);

    /**
     * Verify if a license key is valid for a given hardware ID
     * Returns detailed information about the verification result
     */
    Map<String, Object> verifyLicense(String licenseKey, String hardwareId);

    /**
     * Activate a license key for a given hardware ID
     * Returns detailed information about the activation result
     */
    Map<String, Object> activateLicense(String licenseKey, String hardwareId);

    /**
     * Deactivate a license key for a given hardware ID
     * Returns detailed information about the deactivation result
     */
    Map<String, Object> deactivateLicense(String licenseKey, String hardwareId);

    /**
     * Revoke a license
     */
    boolean revokeLicense(UUID id);

    /**
     * Reinstate a revoked license
     */
    boolean reinstateRevokedLicense(UUID id);

    /**
     * Generate a new license key
     */
    String generateLicenseKey(String prefix);
}