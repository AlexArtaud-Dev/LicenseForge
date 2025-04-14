package com.alexartauddev.licenseforge.application.license.service;


import com.alexartauddev.licenseforge.web.dto.license.ActivationDTO;
import com.alexartauddev.licenseforge.web.dto.license.LicenseDTO;
import com.alexartauddev.licenseforge.web.request.license.CreateLicenseRequest;
import com.alexartauddev.licenseforge.web.request.license.UpdateLicenseRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface LicenseService {
    LicenseDTO createLicense(CreateLicenseRequest request);

    LicenseDTO getLicenseById(UUID id);

    LicenseDTO getLicenseByKey(String licenseKey);

    List<LicenseDTO> getLicensesByAppId(UUID appId, int page, int size);

    List<LicenseDTO> getLicensesByCustomerId(String customerId, int page, int size);

    List<LicenseDTO> getActiveLicensesByAppId(UUID appId, int page, int size);

    List<LicenseDTO> getExpiringLicenses(UUID appId, LocalDateTime start, LocalDateTime end, int page, int size);

    LicenseDTO updateLicense(UUID id, UpdateLicenseRequest request);

    void deleteLicense(UUID id);

    LicenseDTO revokeLicense(UUID id);

    ActivationDTO activateLicense(String licenseKey, String hardwareId);

    boolean deactivateLicense(String licenseKey, String hardwareId);

    boolean validateLicense(String licenseKey, String hardwareId);

    long countActiveLicensesByAppId(UUID appId);
}