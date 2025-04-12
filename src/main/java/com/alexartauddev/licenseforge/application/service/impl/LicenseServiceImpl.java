package com.alexartauddev.licenseforge.application.service.impl;

import com.alexartauddev.licenseforge.application.service.LicenseService;
import com.alexartauddev.licenseforge.domain.license.entity.License;
import com.alexartauddev.licenseforge.domain.license.repository.LicenseRepository;
import com.alexartauddev.licenseforge.web.dto.license.LicenseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class LicenseServiceImpl implements LicenseService {

    private final LicenseRepository licenseRepository;
    private final SecureRandom random;

    @Autowired
    public LicenseServiceImpl(LicenseRepository licenseRepository) {
        this.licenseRepository = licenseRepository;
        this.random = new SecureRandom();
    }

    @Override
    @Transactional
    public LicenseDTO createLicense(String productId, String customerId, int maxActivations, LocalDateTime expiresAt) {
        String licenseKey = generateLicenseKey("LFORG");

        License license = License.builder()
                .licenseKey(licenseKey)
                .productId(productId)
                .customerId(customerId)
                .maxActivations(maxActivations)
                .expiresAt(expiresAt)
                .createdAt(LocalDateTime.now())
                .revoked(false)
                .hardwareIds(new HashSet<>())
                .build();

        License savedLicense = licenseRepository.save(license);
        return mapToDto(savedLicense);
    }

    @Override
    public Optional<LicenseDTO> getLicense(UUID id) {
        return licenseRepository.findById(id).map(this::mapToDto);
    }

    @Override
    public Optional<LicenseDTO> getLicenseByKey(String licenseKey) {
        return licenseRepository.findByLicenseKey(licenseKey).map(this::mapToDto);
    }

    @Override
    public List<LicenseDTO> getLicensesByCustomer(String customerId) {
        return licenseRepository.findByCustomerId(customerId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<LicenseDTO> getLicensesByProduct(String productId) {
        return licenseRepository.findByProductId(productId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, Object> verifyLicense(String licenseKey, String hardwareId) {
        Map<String, Object> result = new HashMap<>();
        Optional<License> licenseOpt = licenseRepository.findByLicenseKey(licenseKey);

        if (licenseOpt.isEmpty()) {
            result.put("success", false);
            result.put("message", "License key not found");
            result.put("errorCode", "LICENSE_NOT_FOUND");
            return result;
        }

        License license = licenseOpt.get();

        // Check if license is expired
        if (license.isExpired()) {
            result.put("success", false);
            result.put("message", "License is expired");
            result.put("errorCode", "LICENSE_EXPIRED");
            result.put("expiryDate", license.getExpiresAt());
            return result;
        }

        // Check if license is revoked
        if (license.isRevoked()) {
            result.put("success", false);
            result.put("message", "License is revoked");
            result.put("errorCode", "LICENSE_REVOKED");
            return result;
        }

        // Check if this hardware ID is already activated
        boolean hwIdActivated = license.getHardwareIds().contains(hardwareId);

        // Check if more activations are allowed
        boolean canActivateMore = license.getHardwareIds().size() < license.getMaxActivations();

        if (hwIdActivated || canActivateMore) {
            result.put("success", true);
            if (hwIdActivated) {
                result.put("message", "Hardware ID is already activated for this license");
                result.put("status", "ALREADY_ACTIVATED");
            } else {
                result.put("message", "License is valid and can be activated");
                result.put("status", "AVAILABLE_FOR_ACTIVATION");
            }
            result.put("activationCount", license.getHardwareIds().size());
            result.put("maxActivations", license.getMaxActivations());
            result.put("expiresAt", license.getExpiresAt());
        } else {
            result.put("success", false);
            result.put("message", "Maximum activations reached");
            result.put("errorCode", "MAX_ACTIVATIONS_REACHED");
            result.put("activationCount", license.getHardwareIds().size());
            result.put("maxActivations", license.getMaxActivations());
        }

        return result;
    }

    @Override
    @Transactional
    public Map<String, Object> activateLicense(String licenseKey, String hardwareId) {
        Map<String, Object> result = new HashMap<>();
        Optional<License> licenseOpt = licenseRepository.findByLicenseKey(licenseKey);

        if (licenseOpt.isEmpty()) {
            result.put("success", false);
            result.put("message", "License key not found");
            result.put("errorCode", "LICENSE_NOT_FOUND");
            return result;
        }

        License license = licenseOpt.get();

        // Check if this hardware ID is already activated
        if (license.getHardwareIds().contains(hardwareId)) {
            result.put("success", true);
            result.put("message", "Hardware ID already activated for this license");
            result.put("activationCount", license.getHardwareIds().size());
            result.put("maxActivations", license.getMaxActivations());
            return result;
        }

        // Check if license is expired
        if (license.isExpired()) {
            result.put("success", false);
            result.put("message", "License is expired");
            result.put("errorCode", "LICENSE_EXPIRED");
            result.put("expiryDate", license.getExpiresAt());
            return result;
        }

        // Check if license is revoked
        if (license.isRevoked()) {
            result.put("success", false);
            result.put("message", "License is revoked");
            result.put("errorCode", "LICENSE_REVOKED");
            return result;
        }

        // Check if max activations reached
        if (license.getHardwareIds().size() >= license.getMaxActivations()) {
            result.put("success", false);
            result.put("message", "Maximum activations reached");
            result.put("errorCode", "MAX_ACTIVATIONS_REACHED");
            result.put("activationCount", license.getHardwareIds().size());
            result.put("maxActivations", license.getMaxActivations());
            return result;
        }

        // All checks passed, activate the license
        license.getHardwareIds().add(hardwareId);
        licenseRepository.save(license);

        result.put("success", true);
        result.put("message", "License successfully activated");
        result.put("activationCount", license.getHardwareIds().size());
        result.put("maxActivations", license.getMaxActivations());

        return result;
    }

    @Override
    @Transactional
    public Map<String, Object> deactivateLicense(String licenseKey, String hardwareId) {
        Map<String, Object> result = new HashMap<>();
        Optional<License> licenseOpt = licenseRepository.findByLicenseKey(licenseKey);

        if (licenseOpt.isEmpty()) {
            result.put("success", false);
            result.put("message", "License key not found");
            result.put("errorCode", "LICENSE_NOT_FOUND");
            return result;
        }

        License license = licenseOpt.get();

        // Check if this hardware ID is activated for this license
        if (!license.getHardwareIds().contains(hardwareId)) {
            result.put("success", false);
            result.put("message", "Hardware ID is not activated for this license");
            result.put("errorCode", "HARDWARE_NOT_ACTIVATED");
            return result;
        }

        // Check if license is revoked
        if (license.isRevoked()) {
            result.put("success", false);
            result.put("message", "Cannot deactivate a revoked license");
            result.put("errorCode", "LICENSE_REVOKED");
            return result;
        }

        // All checks passed, deactivate the license
        boolean removed = license.getHardwareIds().remove(hardwareId);
        licenseRepository.save(license);

        result.put("success", removed);
        if (removed) {
            result.put("message", "License successfully deactivated");
            result.put("activationCount", license.getHardwareIds().size());
            result.put("maxActivations", license.getMaxActivations());
        } else {
            result.put("message", "Failed to deactivate license");
            result.put("errorCode", "DEACTIVATION_FAILED");
        }

        return result;
    }

    @Override
    @Transactional
    public boolean revokeLicense(UUID id) {
        Optional<License> licenseOpt = licenseRepository.findById(id);

        if (licenseOpt.isEmpty()) {
            return false;
        }

        License license = licenseOpt.get();
        license.setRevoked(true);
        licenseRepository.save(license);
        return true;
    }

    @Override
    @Transactional
    public boolean reinstateRevokedLicense(UUID id) {
        Optional<License> licenseOpt = licenseRepository.findById(id);

        if (licenseOpt.isEmpty()) {
            return false;
        }

        License license = licenseOpt.get();
        license.setRevoked(false);
        licenseRepository.save(license);
        return true;
    }

    @Override
    public String generateLicenseKey(String prefix) {
        // Format: PREFIX-XXXX-XXXX-XXXX-XXXX
        StringBuilder key = new StringBuilder(prefix);

        // Add 4 groups of 4 alphanumeric characters
        for (int i = 0; i < 4; i++) {
            key.append("-");
            for (int j = 0; j < 4; j++) {
                key.append(randomAlphaNumeric());
            }
        }

        // Check if this key already exists, if so generate a new one
        if (licenseRepository.existsByLicenseKey(key.toString())) {
            return generateLicenseKey(prefix);
        }

        return key.toString();
    }

    private char randomAlphaNumeric() {
        String chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"; // Removed similar looking characters
        return chars.charAt(random.nextInt(chars.length()));
    }

    private LicenseDTO mapToDto(License license) {
        return LicenseDTO.builder()
                .id(license.getId())
                .licenseKey(license.getLicenseKey())
                .productId(license.getProductId())
                .customerId(license.getCustomerId())
                .createdAt(license.getCreatedAt())
                .expiresAt(license.getExpiresAt())
                .maxActivations(license.getMaxActivations())
                .revoked(license.isRevoked())
                .hardwareIds(license.getHardwareIds())
                .build();
    }
}