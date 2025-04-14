package com.alexartauddev.licenseforge.application.application.service.impl;

import com.alexartauddev.licenseforge.application.application.mapper.ApplicationMapper;
import com.alexartauddev.licenseforge.application.application.service.ApplicationService;
import com.alexartauddev.licenseforge.application.license.service.LicenseService;
import com.alexartauddev.licenseforge.domain.application.entity.Application;
import com.alexartauddev.licenseforge.domain.application.repository.ApplicationRepository;
import com.alexartauddev.licenseforge.web.dto.application.ApplicationDTO;
import com.alexartauddev.licenseforge.web.exception.application.ApplicationNotFoundException;
import com.alexartauddev.licenseforge.web.request.application.CreateApplicationRequest;
import com.alexartauddev.licenseforge.web.request.application.UpdateApplicationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ApplicationServiceImpl implements ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final ApplicationMapper applicationMapper;
    private final LicenseService licenseService;

    @Override
    @Transactional
    public ApplicationDTO createApplication(CreateApplicationRequest request) {
        Application application = Application.builder()
                .name(request.getName())
                .description(request.getDescription())
                .realmId(request.getRealmId())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Application savedApplication = applicationRepository.save(application);

        // Convert to DTO and set active licenses count
        ApplicationDTO dto = applicationMapper.toDTO(savedApplication);
        dto.setActiveLicensesCount(0); // New application has no licenses

        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public ApplicationDTO getApplicationById(UUID id) {
        Application application = applicationRepository.findById(id)
                .orElseThrow(() -> ApplicationNotFoundException.withId(id));

        ApplicationDTO dto = applicationMapper.toDTO(application);
        dto.setActiveLicensesCount(licenseService.countActiveLicensesByAppId(application.getId()));

        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ApplicationDTO> getApplicationsByRealmId(UUID realmId, int page, int size) {
        return applicationRepository.findByRealmId(realmId).stream()
                .skip((long) page * size)
                .limit(size)
                .map(app -> {
                    ApplicationDTO dto = applicationMapper.toDTO(app);
                    dto.setActiveLicensesCount(licenseService.countActiveLicensesByAppId(app.getId()));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ApplicationDTO updateApplication(UUID id, UpdateApplicationRequest request) {
        Application application = applicationRepository.findById(id)
                .orElseThrow(() -> ApplicationNotFoundException.withId(id));

        if (request.getName() != null) {
            application.setName(request.getName());
        }

        if (request.getDescription() != null) {
            application.setDescription(request.getDescription());
        }

        application.setUpdatedAt(LocalDateTime.now());

        Application updatedApplication = applicationRepository.save(application);

        ApplicationDTO dto = applicationMapper.toDTO(updatedApplication);
        dto.setActiveLicensesCount(licenseService.countActiveLicensesByAppId(updatedApplication.getId()));

        return dto;
    }

    @Override
    @Transactional
    public void deleteApplication(UUID id) {
        Application application = applicationRepository.findById(id)
                .orElseThrow(() -> ApplicationNotFoundException.withId(id));

        applicationRepository.delete(application);
    }

    @Override
    @Transactional(readOnly = true)
    public long countByRealmId(UUID realmId) {
        return applicationRepository.countByRealmId(realmId);
    }
}