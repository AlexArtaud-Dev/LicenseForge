package com.alexartauddev.licenseforge.unit.activation.service;

import com.alexartauddev.licenseforge.application.application.mapper.ApplicationMapper;
import com.alexartauddev.licenseforge.application.application.service.impl.ApplicationServiceImpl;
import com.alexartauddev.licenseforge.application.license.service.LicenseService;
import com.alexartauddev.licenseforge.domain.application.entity.Application;
import com.alexartauddev.licenseforge.domain.application.repository.ApplicationRepository;
import com.alexartauddev.licenseforge.web.dto.application.ApplicationDTO;
import com.alexartauddev.licenseforge.web.exception.application.ApplicationNotFoundException;
import com.alexartauddev.licenseforge.web.request.application.CreateApplicationRequest;
import com.alexartauddev.licenseforge.web.request.application.UpdateApplicationRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ActivationServiceImplTest {

    @Mock
    private ApplicationRepository applicationRepository;

    @Mock
    private ApplicationMapper applicationMapper;

    @Mock
    private LicenseService licenseService;

    @InjectMocks
    private ApplicationServiceImpl applicationService;

    private UUID applicationId;
    private UUID realmId;
    private Application application;
    private ApplicationDTO applicationDTO;
    private CreateApplicationRequest createRequest;

    @BeforeEach
    void setUp() {
        applicationId = UUID.randomUUID();
        realmId = UUID.randomUUID();

        application = Application.builder()
                .id(applicationId)
                .name("Test Application")
                .description("Test Application Description")
                .realmId(realmId)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        applicationDTO = ApplicationDTO.builder()
                .id(applicationId)
                .name("Test Application")
                .description("Test Application Description")
                .realmId(realmId)
                .activeLicensesCount(5)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        createRequest = CreateApplicationRequest.builder()
                .name("Test Application")
                .description("Test Application Description")
                .realmId(realmId)
                .build();
    }

    @Test
    void createApplication_ValidRequest_ShouldCreateApplication() {
        // Arrange
        when(applicationRepository.save(any(Application.class))).thenReturn(application);
        when(applicationMapper.toDTO(any(Application.class))).thenReturn(applicationDTO);

        // Act
        ApplicationDTO result = applicationService.createApplication(createRequest);

        // Assert
        assertNotNull(result);
        assertEquals(applicationDTO, result);
        verify(applicationRepository).save(any(Application.class));
        verify(applicationMapper).toDTO(any(Application.class));
    }

    @Test
    void getApplicationById_ExistingApplication_ShouldReturnApplication() {
        // Arrange
        when(applicationRepository.findById(applicationId)).thenReturn(Optional.of(application));
        when(applicationMapper.toDTO(application)).thenReturn(applicationDTO);
        when(licenseService.countActiveLicensesByAppId(applicationId)).thenReturn(5L);

        // Act
        ApplicationDTO result = applicationService.getApplicationById(applicationId);

        // Assert
        assertNotNull(result);
        assertEquals(applicationDTO, result);
        verify(applicationRepository).findById(applicationId);
        verify(applicationMapper).toDTO(application);
        verify(licenseService).countActiveLicensesByAppId(applicationId);
    }

    @Test
    void getApplicationById_NonExistingApplication_ShouldThrowException() {
        // Arrange
        when(applicationRepository.findById(applicationId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ApplicationNotFoundException.class, () -> applicationService.getApplicationById(applicationId));
        verify(applicationRepository).findById(applicationId);
        verify(applicationMapper, never()).toDTO(any(Application.class));
    }

    @Test
    void getApplicationsByRealmId_ShouldReturnApplications() {
        // Arrange
        List<Application> applications = Arrays.asList(application, application);
        when(applicationRepository.findByRealmId(realmId)).thenReturn(applications);
        when(applicationMapper.toDTO(any(Application.class))).thenReturn(applicationDTO);
        when(licenseService.countActiveLicensesByAppId(any(UUID.class))).thenReturn(5L);

        // Act
        List<ApplicationDTO> result = applicationService.getApplicationsByRealmId(realmId, 0, 10);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(applicationDTO, result.get(0));
        verify(applicationRepository).findByRealmId(realmId);
        verify(applicationMapper, times(2)).toDTO(any(Application.class));
        verify(licenseService, times(2)).countActiveLicensesByAppId(any(UUID.class));
    }

    @Test
    void updateApplication_ExistingApplication_ShouldUpdateApplication() {
        // Arrange
        UpdateApplicationRequest request = UpdateApplicationRequest.builder()
                .name("Updated Application")
                .description("Updated Description")
                .build();

        when(applicationRepository.findById(applicationId)).thenReturn(Optional.of(application));
        when(applicationRepository.save(any(Application.class))).thenReturn(application);
        when(applicationMapper.toDTO(application)).thenReturn(applicationDTO);
        when(licenseService.countActiveLicensesByAppId(applicationId)).thenReturn(5L);

        // Act
        ApplicationDTO result = applicationService.updateApplication(applicationId, request);

        // Assert
        assertNotNull(result);
        assertEquals(applicationDTO, result);
        verify(applicationRepository).findById(applicationId);
        verify(applicationRepository).save(application);
        verify(applicationMapper).toDTO(application);
        verify(licenseService).countActiveLicensesByAppId(applicationId);

        // Verify that the application was updated with the new values
        assertEquals("Updated Application", application.getName());
        assertEquals("Updated Description", application.getDescription());
    }

    @Test
    void updateApplication_NonExistingApplication_ShouldThrowException() {
        // Arrange
        UpdateApplicationRequest request = new UpdateApplicationRequest();
        when(applicationRepository.findById(applicationId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ApplicationNotFoundException.class, () -> applicationService.updateApplication(applicationId, request));
        verify(applicationRepository).findById(applicationId);
        verify(applicationRepository, never()).save(any(Application.class));
    }

    @Test
    void deleteApplication_ExistingApplication_ShouldDeleteApplication() {
        // Arrange
        when(applicationRepository.findById(applicationId)).thenReturn(Optional.of(application));

        // Act
        applicationService.deleteApplication(applicationId);

        // Assert
        verify(applicationRepository).findById(applicationId);
        verify(applicationRepository).delete(application);
    }

    @Test
    void deleteApplication_NonExistingApplication_ShouldThrowException() {
        // Arrange
        when(applicationRepository.findById(applicationId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ApplicationNotFoundException.class, () -> applicationService.deleteApplication(applicationId));
        verify(applicationRepository).findById(applicationId);
        verify(applicationRepository, never()).delete(any(Application.class));
    }

    @Test
    void countByRealmId_ShouldReturnCount() {
        // Arrange
        when(applicationRepository.countByRealmId(realmId)).thenReturn(3L);

        // Act
        long result = applicationService.countByRealmId(realmId);

        // Assert
        assertEquals(3L, result);
        verify(applicationRepository).countByRealmId(realmId);
    }
}