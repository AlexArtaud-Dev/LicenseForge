package com.alexartauddev.licenseforge.application.license.service.impl;

import com.alexartauddev.licenseforge.application.license.mapper.LicenseMapper;
import com.alexartauddev.licenseforge.application.license.service.ActivationService;
import com.alexartauddev.licenseforge.domain.license.entity.Activation;
import com.alexartauddev.licenseforge.domain.license.repository.ActivationRepository;
import com.alexartauddev.licenseforge.web.dto.license.ActivationDTO;
import com.alexartauddev.licenseforge.web.exception.license.ActivationNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ActivationServiceImpl implements ActivationService {

    private final ActivationRepository activationRepository;
    private final LicenseMapper licenseMapper;

    @Override
    @Transactional(readOnly = true)
    public List<ActivationDTO> getActivationsByLicenseId(UUID licenseId, int page, int size) {
        return activationRepository.findByLicenseId(licenseId).stream()
                .skip((long) page * size)
                .limit(size)
                .map(licenseMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ActivationDTO getActivationById(UUID id) {
        Activation activation = activationRepository.findById(id)
                .orElseThrow(() -> new ActivationNotFoundException("Activation not found with id: " + id));
        return licenseMapper.toDTO(activation);
    }

    @Override
    @Transactional(readOnly = true)
    public ActivationDTO getActivationByLicenseIdAndHardwareId(UUID licenseId, String hardwareId) {
        Activation activation = activationRepository.findByLicenseIdAndHardwareId(licenseId, hardwareId)
                .orElseThrow(() -> new ActivationNotFoundException(
                        "Activation not found for license " + licenseId + " and hardware " + hardwareId));
        return licenseMapper.toDTO(activation);
    }

    @Override
    @Transactional
    public ActivationDTO updateLastSeen(UUID id) {
        Activation activation = activationRepository.findById(id)
                .orElseThrow(() -> new ActivationNotFoundException("Activation not found with id: " + id));

        activation.updateLastSeen();
        Activation updatedActivation = activationRepository.save(activation);
        return licenseMapper.toDTO(updatedActivation);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ActivationDTO> findInactiveActivations(LocalDateTime threshold, int page, int size) {
        return activationRepository.findInactiveActivations(threshold).stream()
                .skip((long) page * size)
                .limit(size)
                .map(licenseMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteActivation(UUID id) {
        Activation activation = activationRepository.findById(id)
                .orElseThrow(() -> new ActivationNotFoundException("Activation not found with id: " + id));
        activationRepository.delete(activation);
    }

    @Override
    @Transactional(readOnly = true)
    public long countByLicenseId(UUID licenseId) {
        return activationRepository.countByLicenseId(licenseId);
    }
}