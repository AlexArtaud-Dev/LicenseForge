package com.alexartauddev.licenseforge.unit.license.service;

import com.alexartauddev.licenseforge.application.license.mapper.LicenseMapper;
import com.alexartauddev.licenseforge.application.license.service.impl.LicenseServiceImpl;
import com.alexartauddev.licenseforge.domain.license.entity.Activation;
import com.alexartauddev.licenseforge.domain.license.entity.License;
import com.alexartauddev.licenseforge.domain.license.repository.ActivationRepository;
import com.alexartauddev.licenseforge.domain.license.repository.LicenseRepository;
import com.alexartauddev.licenseforge.web.dto.license.ActivationDTO;
import com.alexartauddev.licenseforge.web.dto.license.LicenseDTO;
import com.alexartauddev.licenseforge.web.exception.license.LicenseActivationException;
import com.alexartauddev.licenseforge.web.exception.license.LicenseNotFoundException;
import com.alexartauddev.licenseforge.web.request.license.CreateLicenseRequest;
import com.alexartauddev.licenseforge.web.request.license.UpdateLicenseRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LicenseServiceImplTest {

    @Mock
    private LicenseRepository licenseRepository;

    @Mock
    private ActivationRepository activationRepository;

    @Mock
    private LicenseMapper licenseMapper;

    @InjectMocks
    private LicenseServiceImpl licenseService;

    private UUID licenseId;
    private UUID appId;
    private String licenseKey;
    private String hardwareId;
    private License license;
    private LicenseDTO licenseDTO;
    private Activation activation;
    private ActivationDTO activationDTO;

    @BeforeEach
    void setUp() {
        licenseId = UUID.randomUUID();
        appId = UUID.randomUUID();
        licenseKey = "APP1-ABCD-EFGH-IJKL-MNOP";
        hardwareId = "HARDWARE-ABC-123";

        license = License.builder()
                .id(licenseId)
                .licenseKey(licenseKey)
                .appId(appId)
                .customerId("customer123")
                .expiresAt(LocalDateTime.now().plusMonths(12))
                .maxActivations(3)
                .revoked(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .hardwareIds(new HashSet<>())
                .build();

        licenseDTO = LicenseDTO.builder()
                .id(licenseId)
                .licenseKey(licenseKey)
                .appId(appId)
                .customerId("customer123")
                .expiresAt(LocalDateTime.now().plusMonths(12))
                .maxActivations(3)
                .revoked(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .hardwareIds(new HashSet<>())
                .expired(false)
                .activationsCount(0)
                .build();

        activation = Activation.builder()
                .id(UUID.randomUUID())
                .licenseId(licenseId)
                .hardwareId(hardwareId)
                .activatedAt(LocalDateTime.now())
                .lastSeenAt(LocalDateTime.now())
                .build();

        activationDTO = ActivationDTO.builder()
                .id(activation.getId())
                .licenseId(licenseId)
                .hardwareId(hardwareId)
                .activatedAt(LocalDateTime.now())
                .lastSeenAt(LocalDateTime.now())
                .build();
    }

    @Test
    void createLicense_ShouldCreateNewLicense() {
        // Arrange
        CreateLicenseRequest request = CreateLicenseRequest.builder()
                .appId(appId)
                .customerId("customer123")
                .expiresAt(LocalDateTime.now().plusMonths(12))
                .maxActivations(3)
                .build();

        when(licenseRepository.save(any(License.class))).thenReturn(license);
        when(licenseMapper.toDTO(any(License.class))).thenReturn(licenseDTO);

        // Act
        LicenseDTO result = licenseService.createLicense(request);

        // Assert
        assertNotNull(result);
        assertEquals(licenseDTO, result);
        verify(licenseRepository).save(any(License.class));
        verify(licenseMapper).toDTO(any(License.class));
    }

    @Test
    void getLicenseById_ExistingLicense_ShouldReturnLicense() {
        // Arrange
        when(licenseRepository.findById(licenseId)).thenReturn(Optional.of(license));
        when(licenseMapper.toDTO(license)).thenReturn(licenseDTO);
        when(activationRepository.countByLicenseId(licenseId)).thenReturn(0L);

        // Act
        LicenseDTO result = licenseService.getLicenseById(licenseId);

        // Assert
        assertNotNull(result);
        assertEquals(licenseDTO, result);
        verify(licenseRepository).findById(licenseId);
        verify(licenseMapper).toDTO(license);
        verify(activationRepository).countByLicenseId(licenseId);
    }

    @Test
    void getLicenseById_NonExistingLicense_ShouldThrowException() {
        // Arrange
        when(licenseRepository.findById(licenseId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(LicenseNotFoundException.class, () -> licenseService.getLicenseById(licenseId));
        verify(licenseRepository).findById(licenseId);
    }

    @Test
    void getLicenseByKey_ExistingLicense_ShouldReturnLicense() {
        // Arrange
        when(licenseRepository.findByLicenseKey(licenseKey)).thenReturn(Optional.of(license));
        when(licenseMapper.toDTO(license)).thenReturn(licenseDTO);
        when(activationRepository.countByLicenseId(licenseId)).thenReturn(0L);

        // Act
        LicenseDTO result = licenseService.getLicenseByKey(licenseKey);

        // Assert
        assertNotNull(result);
        assertEquals(licenseDTO, result);
        verify(licenseRepository).findByLicenseKey(licenseKey);
        verify(licenseMapper).toDTO(license);
        verify(activationRepository).countByLicenseId(licenseId);
    }

    @Test
    void getLicenseByKey_NonExistingLicense_ShouldThrowException() {
        // Arrange
        when(licenseRepository.findByLicenseKey(licenseKey)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(LicenseNotFoundException.class, () -> licenseService.getLicenseByKey(licenseKey));
        verify(licenseRepository).findByLicenseKey(licenseKey);
    }

    @Test
    void getLicensesByAppId_ShouldReturnLicenses() {
        // Arrange
        List<License> licenses = new ArrayList<>();
        licenses.add(license);

        when(licenseRepository.findByAppId(appId)).thenReturn(licenses);
        when(licenseMapper.toDTO(license)).thenReturn(licenseDTO);
        when(activationRepository.countByLicenseId(licenseId)).thenReturn(0L);

        // Act
        List<LicenseDTO> result = licenseService.getLicensesByAppId(appId, 0, 10);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(licenseDTO, result.get(0));
        verify(licenseRepository).findByAppId(appId);
        verify(licenseMapper).toDTO(license);
        verify(activationRepository).countByLicenseId(licenseId);
    }

    @Test
    void updateLicense_ExistingLicense_ShouldUpdateLicense() {
        // Arrange
        UpdateLicenseRequest request = UpdateLicenseRequest.builder()
                .customerId("updatedCustomer")
                .expiresAt(LocalDateTime.now().plusYears(1))
                .maxActivations(5)
                .revoked(false)
                .build();

        when(licenseRepository.findById(licenseId)).thenReturn(Optional.of(license));
        when(licenseRepository.save(any(License.class))).thenReturn(license);
        when(licenseMapper.toDTO(license)).thenReturn(licenseDTO);
        when(activationRepository.countByLicenseId(licenseId)).thenReturn(0L);

        // Act
        LicenseDTO result = licenseService.updateLicense(licenseId, request);

        // Assert
        assertNotNull(result);
        assertEquals(licenseDTO, result);
        verify(licenseRepository).findById(licenseId);
        verify(licenseRepository).save(license);
        verify(licenseMapper).toDTO(license);
        verify(activationRepository).countByLicenseId(licenseId);

        // Verify that the license was updated with the new values
        assertEquals("updatedCustomer", license.getCustomerId());
        assertEquals(5, license.getMaxActivations());
    }

    @Test
    void updateLicense_NonExistingLicense_ShouldThrowException() {
        // Arrange
        UpdateLicenseRequest request = new UpdateLicenseRequest();
        when(licenseRepository.findById(licenseId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(LicenseNotFoundException.class, () -> licenseService.updateLicense(licenseId, request));
        verify(licenseRepository).findById(licenseId);
    }

    @Test
    void deleteLicense_ExistingLicense_ShouldDeleteLicense() {
        // Arrange
        when(licenseRepository.findById(licenseId)).thenReturn(Optional.of(license));
        // The delete and deleteByLicenseId methods are void, so we don't need to stub them with doNothing()
        // Just verify they're called later

        // Act
        licenseService.deleteLicense(licenseId);

        // Assert
        verify(licenseRepository).findById(licenseId);
        verify(activationRepository).deleteByLicenseId(licenseId);
        verify(licenseRepository).delete(license);
    }

    @Test
    void deleteLicense_NonExistingLicense_ShouldThrowException() {
        // Arrange
        when(licenseRepository.findById(licenseId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(LicenseNotFoundException.class, () -> licenseService.deleteLicense(licenseId));
        verify(licenseRepository).findById(licenseId);
        verify(activationRepository, never()).deleteByLicenseId(any(UUID.class));
        verify(licenseRepository, never()).delete(any(License.class));
    }

    @Test
    void revokeLicense_ExistingLicense_ShouldRevokeLicense() {
        // Arrange
        when(licenseRepository.findById(licenseId)).thenReturn(Optional.of(license));
        when(licenseRepository.save(any(License.class))).thenReturn(license);
        when(licenseMapper.toDTO(license)).thenReturn(licenseDTO);
        when(activationRepository.countByLicenseId(licenseId)).thenReturn(0L);

        // Act
        LicenseDTO result = licenseService.revokeLicense(licenseId);

        // Assert
        assertNotNull(result);
        assertEquals(licenseDTO, result);
        verify(licenseRepository).findById(licenseId);
        verify(licenseRepository).save(license);
        verify(licenseMapper).toDTO(license);
        verify(activationRepository).countByLicenseId(licenseId);

        // Verify that the license was revoked
        assertTrue(license.isRevoked());
    }

    @Test
    void activateLicense_ValidLicense_ShouldActivateLicense() {
        // Arrange
        when(licenseRepository.findByLicenseKey(licenseKey)).thenReturn(Optional.of(license));
        when(activationRepository.findByLicenseIdAndHardwareId(licenseId, hardwareId)).thenReturn(Optional.empty());
        when(activationRepository.countByLicenseId(licenseId)).thenReturn(0L);
        when(activationRepository.save(any(Activation.class))).thenReturn(activation);
        when(licenseRepository.save(any(License.class))).thenReturn(license);
        when(licenseMapper.toDTO(any(Activation.class))).thenReturn(activationDTO);

        // Act
        ActivationDTO result = licenseService.activateLicense(licenseKey, hardwareId);

        // Assert
        assertNotNull(result);
        assertEquals(activationDTO, result);
        verify(licenseRepository).findByLicenseKey(licenseKey);
        verify(activationRepository).findByLicenseIdAndHardwareId(licenseId, hardwareId);
        verify(activationRepository).countByLicenseId(licenseId);
        verify(activationRepository).save(any(Activation.class));
        verify(licenseRepository).save(license);
        verify(licenseMapper).toDTO(any(Activation.class));

        // Verify that the license activated the hardware ID
        assertTrue(license.getHardwareIds().contains(hardwareId));
    }

    @Test
    void activateLicense_ExpiredLicense_ShouldThrowException() {
        // Arrange
        License expiredLicense = License.builder()
                .id(licenseId)
                .licenseKey(licenseKey)
                .appId(appId)
                .customerId("customer123")
                .expiresAt(LocalDateTime.now().minusDays(1)) // Expired
                .maxActivations(3)
                .revoked(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .hardwareIds(new HashSet<>())
                .build();

        when(licenseRepository.findByLicenseKey(licenseKey)).thenReturn(Optional.of(expiredLicense));

        // Act & Assert
        LicenseActivationException exception = assertThrows(LicenseActivationException.class,
                () -> licenseService.activateLicense(licenseKey, hardwareId));
        assertEquals("License has expired", exception.getMessage());
        verify(licenseRepository).findByLicenseKey(licenseKey);
        verify(activationRepository, never()).findByLicenseIdAndHardwareId(any(UUID.class), anyString());
    }

    @Test
    void activateLicense_RevokedLicense_ShouldThrowException() {
        // Arrange
        License revokedLicense = License.builder()
                .id(licenseId)
                .licenseKey(licenseKey)
                .appId(appId)
                .customerId("customer123")
                .expiresAt(LocalDateTime.now().plusMonths(12))
                .maxActivations(3)
                .revoked(true) // Revoked
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .hardwareIds(new HashSet<>())
                .build();

        when(licenseRepository.findByLicenseKey(licenseKey)).thenReturn(Optional.of(revokedLicense));

        // Act & Assert
        LicenseActivationException exception = assertThrows(LicenseActivationException.class,
                () -> licenseService.activateLicense(licenseKey, hardwareId));
        assertEquals("License has been revoked", exception.getMessage());
        verify(licenseRepository).findByLicenseKey(licenseKey);
        verify(activationRepository, never()).findByLicenseIdAndHardwareId(any(UUID.class), anyString());
    }

    @Test
    void activateLicense_MaxActivationsReached_ShouldThrowException() {
        // Arrange
        when(licenseRepository.findByLicenseKey(licenseKey)).thenReturn(Optional.of(license));
        when(activationRepository.findByLicenseIdAndHardwareId(licenseId, hardwareId)).thenReturn(Optional.empty());
        when(activationRepository.countByLicenseId(licenseId)).thenReturn(3L); // Max activations reached

        // Act & Assert
        LicenseActivationException exception = assertThrows(LicenseActivationException.class,
                () -> licenseService.activateLicense(licenseKey, hardwareId));
        assertEquals("Maximum number of activations reached", exception.getMessage());
        verify(licenseRepository).findByLicenseKey(licenseKey);
        verify(activationRepository).findByLicenseIdAndHardwareId(licenseId, hardwareId);
        verify(activationRepository).countByLicenseId(licenseId);
        verify(activationRepository, never()).save(any(Activation.class));
    }

    @Test
    void deactivateLicense_ExistingActivation_ShouldRemoveActivation() {
        // Arrange
        Set<String> hardwareIds = new HashSet<>();
        hardwareIds.add(hardwareId);
        license.setHardwareIds(hardwareIds);

        when(licenseRepository.findByLicenseKey(licenseKey)).thenReturn(Optional.of(license));
        when(activationRepository.findByLicenseIdAndHardwareId(licenseId, hardwareId)).thenReturn(Optional.of(activation));
        doNothing().when(activationRepository).delete(activation);
        when(licenseRepository.save(license)).thenReturn(license);

        // Act
        boolean result = licenseService.deactivateLicense(licenseKey, hardwareId);

        // Assert
        assertTrue(result);
        verify(licenseRepository).findByLicenseKey(licenseKey);
        verify(activationRepository).findByLicenseIdAndHardwareId(licenseId, hardwareId);
        verify(activationRepository).delete(activation);
        verify(licenseRepository).save(license);

        // Verify that the hardware ID was removed from the license
        assertFalse(license.getHardwareIds().contains(hardwareId));
    }

    @Test
    void deactivateLicense_NonExistingActivation_ShouldReturnFalse() {
        // Arrange
        when(licenseRepository.findByLicenseKey(licenseKey)).thenReturn(Optional.of(license));
        when(activationRepository.findByLicenseIdAndHardwareId(licenseId, hardwareId)).thenReturn(Optional.empty());

        // Act
        boolean result = licenseService.deactivateLicense(licenseKey, hardwareId);

        // Assert
        assertFalse(result);
        verify(licenseRepository).findByLicenseKey(licenseKey);
        verify(activationRepository).findByLicenseIdAndHardwareId(licenseId, hardwareId);
        verify(activationRepository, never()).delete(any(Activation.class));
        verify(licenseRepository, never()).save(any(License.class));
    }

    @Test
    void validateLicense_ValidLicense_ShouldReturnTrue() {
        // Arrange
        when(licenseRepository.findByLicenseKey(licenseKey)).thenReturn(Optional.of(license));
        when(activationRepository.findByLicenseIdAndHardwareId(licenseId, hardwareId)).thenReturn(Optional.of(activation));

        // Act
        boolean result = licenseService.validateLicense(licenseKey, hardwareId);

        // Assert
        assertTrue(result);
        verify(licenseRepository).findByLicenseKey(licenseKey);
        verify(activationRepository).findByLicenseIdAndHardwareId(licenseId, hardwareId);
        verify(activationRepository).save(activation); // Verify that last seen timestamp is updated
    }

    @Test
    void validateLicense_NonExistingLicense_ShouldReturnFalse() {
        // Arrange
        when(licenseRepository.findByLicenseKey(licenseKey)).thenReturn(Optional.empty());

        // Act
        boolean result = licenseService.validateLicense(licenseKey, hardwareId);

        // Assert
        assertFalse(result);
        verify(licenseRepository).findByLicenseKey(licenseKey);
        verify(activationRepository, never()).findByLicenseIdAndHardwareId(any(UUID.class), anyString());
    }

    @Test
    void validateLicense_ExpiredLicense_ShouldReturnFalse() {
        // Arrange
        License expiredLicense = License.builder()
                .id(licenseId)
                .licenseKey(licenseKey)
                .appId(appId)
                .customerId("customer123")
                .expiresAt(LocalDateTime.now().minusDays(1)) // Expired
                .maxActivations(3)
                .revoked(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .hardwareIds(new HashSet<>())
                .build();

        when(licenseRepository.findByLicenseKey(licenseKey)).thenReturn(Optional.of(expiredLicense));

        // Act
        boolean result = licenseService.validateLicense(licenseKey, hardwareId);

        // Assert
        assertFalse(result);
        verify(licenseRepository).findByLicenseKey(licenseKey);
    }

    @Test
    void validateLicense_RevokedLicense_ShouldReturnFalse() {
        // Arrange
        License revokedLicense = License.builder()
                .id(licenseId)
                .licenseKey(licenseKey)
                .appId(appId)
                .customerId("customer123")
                .expiresAt(LocalDateTime.now().plusMonths(12))
                .maxActivations(3)
                .revoked(true) // Revoked
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .hardwareIds(new HashSet<>())
                .build();

        when(licenseRepository.findByLicenseKey(licenseKey)).thenReturn(Optional.of(revokedLicense));

        // Act
        boolean result = licenseService.validateLicense(licenseKey, hardwareId);

        // Assert
        assertFalse(result);
        verify(licenseRepository).findByLicenseKey(licenseKey);
    }

    @Test
    void validateLicense_NonActivatedHardware_ShouldReturnFalse() {
        // Arrange
        when(licenseRepository.findByLicenseKey(licenseKey)).thenReturn(Optional.of(license));
        when(activationRepository.findByLicenseIdAndHardwareId(licenseId, hardwareId)).thenReturn(Optional.empty());

        // Act
        boolean result = licenseService.validateLicense(licenseKey, hardwareId);

        // Assert
        assertFalse(result);
        verify(licenseRepository).findByLicenseKey(licenseKey);
        verify(activationRepository).findByLicenseIdAndHardwareId(licenseId, hardwareId);
    }

    @Test
    void countActiveLicensesByAppId_ShouldReturnCount() {
        // Arrange
        LocalDateTime now = LocalDateTime.now();
        when(licenseRepository.countActiveByAppId(eq(appId), any(LocalDateTime.class))).thenReturn(5L);

        // Act
        long result = licenseService.countActiveLicensesByAppId(appId);

        // Assert
        assertEquals(5L, result);
        verify(licenseRepository).countActiveByAppId(eq(appId), any(LocalDateTime.class));
    }
}
