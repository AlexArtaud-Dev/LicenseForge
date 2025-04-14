package com.alexartauddev.licenseforge.application.license.service.impl;

import com.alexartauddev.licenseforge.application.license.mapper.LicenseMapper;
import com.alexartauddev.licenseforge.application.license.service.LicenseService;
import com.alexartauddev.licenseforge.domain.license.entity.Activation;
import com.alexartauddev.licenseforge.domain.license.entity.License;
import com.alexartauddev.licenseforge.domain.license.repository.ActivationRepository;
import com.alexartauddev.licenseforge.domain.license.repository.LicenseRepository;
import com.alexartauddev.licenseforge.domain.license.valueobject.LicenseKey;
import com.alexartauddev.licenseforge.web.dto.license.ActivationDTO;
import com.alexartauddev.licenseforge.web.dto.license.LicenseDTO;
import com.alexartauddev.licenseforge.web.exception.license.LicenseActivationException;
import com.alexartauddev.licenseforge.web.exception.license.LicenseNotFoundException;
import com.alexartauddev.licenseforge.web.exception.license.LicenseValidationException;
import com.alexartauddev.licenseforge.web.request.license.CreateLicenseRequest;
import com.alexartauddev.licenseforge.web.request.license.UpdateLicenseRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LicenseServiceImpl implements LicenseService {

    private final LicenseRepository licenseRepository;
    private final ActivationRepository activationRepository;
    private final LicenseMapper licenseMapper;

    @Override
    @Transactional
    public LicenseDTO createLicense(CreateLicenseRequest request) {
        // Generate a license key with app ID as prefix
        String prefix = request.getAppId().toString().substring(0, 4);
        String licenseKey = LicenseKey.generate(prefix).getValue();

        License license = License.builder()
                .licenseKey(licenseKey)
                .appId(request.getAppId())
                .customerId(request.getCustomerId())
                .expiresAt(request.getExpiresAt())
                .maxActivations(request.getMaxActivations())
                .revoked(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        License savedLicense = licenseRepository.save(license);
        LicenseDTO dto = licenseMapper.toDTO(savedLicense);
        dto.setActivationsCount(0); // New license has no activations
        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public LicenseDTO getLicenseById(UUID id) {
        License license = licenseRepository.findById(id)
                .orElseThrow(() -> LicenseNotFoundException.withId(id));

        LicenseDTO dto = licenseMapper.toDTO(license);
        dto.setActivationsCount(activationRepository.countByLicenseId(license.getId()));
        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public LicenseDTO getLicenseByKey(String licenseKey) {
        License license = licenseRepository.findByLicenseKey(licenseKey)
                .orElseThrow(() -> LicenseNotFoundException.withKey(licenseKey));

        LicenseDTO dto = licenseMapper.toDTO(license);
        dto.setActivationsCount(activationRepository.countByLicenseId(license.getId()));
        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<LicenseDTO> getLicensesByAppId(UUID appId, int page, int size) {
        return licenseRepository.findByAppId(appId).stream()
                .skip((long) page * size)
                .limit(size)
                .map(license -> {
                    LicenseDTO dto = licenseMapper.toDTO(license);
                    dto.setActivationsCount(activationRepository.countByLicenseId(license.getId()));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<LicenseDTO> getLicensesByCustomerId(String customerId, int page, int size) {
        return licenseRepository.findByCustomerId(customerId).stream()
                .skip((long) page * size)
                .limit(size)
                .map(license -> {
                    LicenseDTO dto = licenseMapper.toDTO(license);
                    dto.setActivationsCount(activationRepository.countByLicenseId(license.getId()));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<LicenseDTO> getActiveLicensesByAppId(UUID appId, int page, int size) {
        LocalDateTime now = LocalDateTime.now();
        return licenseRepository.findActiveByAppId(appId, now).stream()
                .skip((long) page * size)
                .limit(size)
                .map(license -> {
                    LicenseDTO dto = licenseMapper.toDTO(license);
                    dto.setActivationsCount(activationRepository.countByLicenseId(license.getId()));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<LicenseDTO> getExpiringLicenses(UUID appId, LocalDateTime start, LocalDateTime end, int page, int size) {
        return licenseRepository.findExpiringLicenses(appId, start, end).stream()
                .skip((long) page * size)
                .limit(size)
                .map(license -> {
                    LicenseDTO dto = licenseMapper.toDTO(license);
                    dto.setActivationsCount(activationRepository.countByLicenseId(license.getId()));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public LicenseDTO updateLicense(UUID id, UpdateLicenseRequest request) {
        License license = licenseRepository.findById(id)
                .orElseThrow(() -> LicenseNotFoundException.withId(id));

        if (request.getCustomerId() != null) {
            license.setCustomerId(request.getCustomerId());
        }

        if (request.getExpiresAt() != null) {
            license.setExpiresAt(request.getExpiresAt());
        }

        if (request.getMaxActivations() != null) {
            license.setMaxActivations(request.getMaxActivations());
        }

        if (request.getRevoked() != null) {
            license.setRevoked(request.getRevoked());
        }

        license.setUpdatedAt(LocalDateTime.now());

        License updatedLicense = licenseRepository.save(license);
        LicenseDTO dto = licenseMapper.toDTO(updatedLicense);
        dto.setActivationsCount(activationRepository.countByLicenseId(license.getId()));
        return dto;
    }

    @Override
    @Transactional
    public void deleteLicense(UUID id) {
        License license = licenseRepository.findById(id)
                .orElseThrow(() -> LicenseNotFoundException.withId(id));

        // Delete all activations for this license
        activationRepository.deleteByLicenseId(license.getId());

        // Delete the license
        licenseRepository.delete(license);
    }

    @Override
    @Transactional
    public LicenseDTO revokeLicense(UUID id) {
        License license = licenseRepository.findById(id)
                .orElseThrow(() -> LicenseNotFoundException.withId(id));

        license.setRevoked(true);
        license.setUpdatedAt(LocalDateTime.now());

        License updatedLicense = licenseRepository.save(license);
        LicenseDTO dto = licenseMapper.toDTO(updatedLicense);
        dto.setActivationsCount(activationRepository.countByLicenseId(license.getId()));
        return dto;
    }

    @Override
    @Transactional
    public ActivationDTO activateLicense(String licenseKey, String hardwareId) {
        License license = licenseRepository.findByLicenseKey(licenseKey)
                .orElseThrow(() -> LicenseNotFoundException.withKey(licenseKey));

        if (license.isExpired()) {
            throw new LicenseActivationException("License has expired");
        }

        if (license.isRevoked()) {
            throw new LicenseActivationException("License has been revoked");
        }

        // Check if this hardware ID is already activated for this license
        Optional<Activation> existingActivation = activationRepository.findByLicenseIdAndHardwareId(license.getId(), hardwareId);
        if (existingActivation.isPresent()) {
            // Update the last seen timestamp
            Activation activation = existingActivation.get();
            activation.updateLastSeen();
            return licenseMapper.toDTO(activationRepository.save(activation));
        }

        // Check if we've reached the maximum number of activations
        long currentActivations = activationRepository.countByLicenseId(license.getId());
        if (currentActivations >= license.getMaxActivations()) {
            throw new LicenseActivationException("Maximum number of activations reached");
        }

        // Create a new activation
        Activation activation = Activation.builder()
                .licenseId(license.getId())
                .hardwareId(hardwareId)
                .build();

        Activation savedActivation = activationRepository.save(activation);

        // Add the hardware ID to the license
        license.activate(hardwareId);
        licenseRepository.save(license);

        return licenseMapper.toDTO(savedActivation);
    }

    @Override
    @Transactional
    public boolean deactivateLicense(String licenseKey, String hardwareId) {
        License license = licenseRepository.findByLicenseKey(licenseKey)
                .orElseThrow(() -> LicenseNotFoundException.withKey(licenseKey));

        Optional<Activation> activation = activationRepository.findByLicenseIdAndHardwareId(license.getId(), hardwareId);
        if (activation.isEmpty()) {
            return false;
        }

        activationRepository.delete(activation.get());

        // Remove the hardware ID from the license
        license.deactivate(hardwareId);
        licenseRepository.save(license);

        return true;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean validateLicense(String licenseKey, String hardwareId) {
        try {
            License license = licenseRepository.findByLicenseKey(licenseKey)
                    .orElseThrow(() -> new LicenseValidationException("Invalid license key"));

            if (license.isExpired()) {
                throw new LicenseValidationException("License has expired");
            }

            if (license.isRevoked()) {
                throw new LicenseValidationException("License has been revoked");
            }

            Optional<Activation> activation = activationRepository.findByLicenseIdAndHardwareId(license.getId(), hardwareId);
            if (activation.isEmpty()) {
                throw new LicenseValidationException("License not activated for this hardware");
            }

            // Update the last seen timestamp
            Activation act = activation.get();
            act.updateLastSeen();
            activationRepository.save(act);

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public long countActiveLicensesByAppId(UUID appId) {
        LocalDateTime now = LocalDateTime.now();
        return licenseRepository.countActiveByAppId(appId, now);
    }
}
