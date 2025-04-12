package com.alexartauddev.licenseforge.application.service.impl;

import com.alexartauddev.licenseforge.domain.license.entity.License;
import com.alexartauddev.licenseforge.domain.license.repository.LicenseRepository;
import com.alexartauddev.licenseforge.web.dto.license.LicenseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LicenseServiceImplTest {

    @Mock
    private LicenseRepository licenseRepository;

    @InjectMocks
    private LicenseServiceImpl licenseService;

    private License testLicense;
    private final UUID licenseId = UUID.randomUUID();
    private final String licenseKey = "LFORG-TEST-1234-5678-9ABC";
    private final String productId = "test-product";
    private final String customerId = "test-customer";
    private final int maxActivations = 3;
    private final String hardwareId = "test-hardware-id";

    @BeforeEach
    void setUp() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime futureDate = now.plusMonths(12);

        testLicense = License.builder()
                .id(licenseId)
                .licenseKey(licenseKey)
                .productId(productId)
                .customerId(customerId)
                .createdAt(now)
                .expiresAt(futureDate)
                .maxActivations(maxActivations)
                .revoked(false)
                .hardwareIds(new HashSet<>())
                .build();
    }

    @Test
    void createLicense_ShouldReturnLicenseDTO() {
        // Arrange
        LocalDateTime expiresAt = LocalDateTime.now().plusMonths(12);
        when(licenseRepository.save(any(License.class))).thenReturn(testLicense);
        when(licenseRepository.existsByLicenseKey(any())).thenReturn(false);

        // Act
        LicenseDTO result = licenseService.createLicense(productId, customerId, maxActivations, expiresAt);

        // Assert
        assertNotNull(result);
        assertEquals(testLicense.getId(), result.getId());
        assertEquals(testLicense.getLicenseKey(), result.getLicenseKey());
        assertEquals(productId, result.getProductId());
        assertEquals(customerId, result.getCustomerId());
        assertEquals(maxActivations, result.getMaxActivations());
        assertFalse(result.isRevoked());
        verify(licenseRepository, times(1)).save(any(License.class));
    }

    @Test
    void getLicense_ShouldReturnLicenseDTO_WhenLicenseExists() {
        // Arrange
        when(licenseRepository.findById(licenseId)).thenReturn(Optional.of(testLicense));

        // Act
        Optional<LicenseDTO> result = licenseService.getLicense(licenseId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(licenseId, result.get().getId());
        verify(licenseRepository, times(1)).findById(licenseId);
    }

    @Test
    void getLicense_ShouldReturnEmpty_WhenLicenseDoesNotExist() {
        // Arrange
        when(licenseRepository.findById(licenseId)).thenReturn(Optional.empty());

        // Act
        Optional<LicenseDTO> result = licenseService.getLicense(licenseId);

        // Assert
        assertFalse(result.isPresent());
        verify(licenseRepository, times(1)).findById(licenseId);
    }

    @Test
    void verifyLicense_ShouldReturnSuccess_WhenLicenseIsValidAndNotActivated() {
        // Arrange
        when(licenseRepository.findByLicenseKey(licenseKey)).thenReturn(Optional.of(testLicense));

        // Act
        Map<String, Object> result = licenseService.verifyLicense(licenseKey, hardwareId);

        // Assert
        assertTrue((Boolean) result.get("success"));
        assertEquals("License is valid and can be activated", result.get("message"));
        assertEquals("AVAILABLE_FOR_ACTIVATION", result.get("status"));
        assertEquals(0, result.get("activationCount"));
        assertEquals(maxActivations, result.get("maxActivations"));
        verify(licenseRepository, times(1)).findByLicenseKey(licenseKey);
    }

    @Test
    void verifyLicense_ShouldReturnSuccess_WhenLicenseIsValidAndAlreadyActivated() {
        // Arrange
        testLicense.getHardwareIds().add(hardwareId);
        when(licenseRepository.findByLicenseKey(licenseKey)).thenReturn(Optional.of(testLicense));

        // Act
        Map<String, Object> result = licenseService.verifyLicense(licenseKey, hardwareId);

        // Assert
        assertTrue((Boolean) result.get("success"));
        assertEquals("Hardware ID is already activated for this license", result.get("message"));
        assertEquals("ALREADY_ACTIVATED", result.get("status"));
        assertEquals(1, result.get("activationCount"));
        assertEquals(maxActivations, result.get("maxActivations"));
        verify(licenseRepository, times(1)).findByLicenseKey(licenseKey);
    }

    @Test
    void verifyLicense_ShouldReturnError_WhenLicenseIsExpired() {
        // Arrange
        testLicense.setExpiresAt(LocalDateTime.now().minusDays(1)); // Expired yesterday
        when(licenseRepository.findByLicenseKey(licenseKey)).thenReturn(Optional.of(testLicense));

        // Act
        Map<String, Object> result = licenseService.verifyLicense(licenseKey, hardwareId);

        // Assert
        assertFalse((Boolean) result.get("success"));
        assertEquals("License is expired", result.get("message"));
        assertEquals("LICENSE_EXPIRED", result.get("errorCode"));
        assertNotNull(result.get("expiryDate"));
        verify(licenseRepository, times(1)).findByLicenseKey(licenseKey);
    }

    @Test
    void verifyLicense_ShouldReturnError_WhenLicenseIsRevoked() {
        // Arrange
        testLicense.setRevoked(true);
        when(licenseRepository.findByLicenseKey(licenseKey)).thenReturn(Optional.of(testLicense));

        // Act
        Map<String, Object> result = licenseService.verifyLicense(licenseKey, hardwareId);

        // Assert
        assertFalse((Boolean) result.get("success"));
        assertEquals("License is revoked", result.get("message"));
        assertEquals("LICENSE_REVOKED", result.get("errorCode"));
        verify(licenseRepository, times(1)).findByLicenseKey(licenseKey);
    }

    @Test
    void verifyLicense_ShouldReturnError_WhenLicenseNotFound() {
        // Arrange
        when(licenseRepository.findByLicenseKey(licenseKey)).thenReturn(Optional.empty());

        // Act
        Map<String, Object> result = licenseService.verifyLicense(licenseKey, hardwareId);

        // Assert
        assertFalse((Boolean) result.get("success"));
        assertEquals("License key not found", result.get("message"));
        assertEquals("LICENSE_NOT_FOUND", result.get("errorCode"));
        verify(licenseRepository, times(1)).findByLicenseKey(licenseKey);
    }

    @Test
    void verifyLicense_ShouldReturnError_WhenMaxActivationsReached() {
        // Arrange
        // Add max activations but with different hardware IDs
        testLicense.getHardwareIds().add("hardware-id-1");
        testLicense.getHardwareIds().add("hardware-id-2");
        testLicense.getHardwareIds().add("hardware-id-3");
        when(licenseRepository.findByLicenseKey(licenseKey)).thenReturn(Optional.of(testLicense));

        // Act
        Map<String, Object> result = licenseService.verifyLicense(licenseKey, "new-hardware-id");

        // Assert
        assertFalse((Boolean) result.get("success"));
        assertEquals("Maximum activations reached", result.get("message"));
        assertEquals("MAX_ACTIVATIONS_REACHED", result.get("errorCode"));
        assertEquals(3, result.get("activationCount"));
        assertEquals(maxActivations, result.get("maxActivations"));
        verify(licenseRepository, times(1)).findByLicenseKey(licenseKey);
    }

    @Test
    void activateLicense_ShouldActivateAndReturnSuccess_WhenLicenseIsValid() {
        // Arrange
        when(licenseRepository.findByLicenseKey(licenseKey)).thenReturn(Optional.of(testLicense));
        when(licenseRepository.save(any(License.class))).thenReturn(testLicense);

        // Act
        Map<String, Object> result = licenseService.activateLicense(licenseKey, hardwareId);

        // Assert
        assertTrue((Boolean) result.get("success"));
        assertEquals("License successfully activated", result.get("message"));
        assertEquals(1, result.get("activationCount"));
        assertEquals(maxActivations, result.get("maxActivations"));
        assertTrue(testLicense.getHardwareIds().contains(hardwareId));
        verify(licenseRepository, times(1)).findByLicenseKey(licenseKey);
        verify(licenseRepository, times(1)).save(testLicense);
    }

    @Test
    void activateLicense_ShouldReturnSuccess_WhenHardwareIdAlreadyActivated() {
        // Arrange
        testLicense.getHardwareIds().add(hardwareId);
        when(licenseRepository.findByLicenseKey(licenseKey)).thenReturn(Optional.of(testLicense));

        // Act
        Map<String, Object> result = licenseService.activateLicense(licenseKey, hardwareId);

        // Assert
        assertTrue((Boolean) result.get("success"));
        assertEquals("Hardware ID already activated for this license", result.get("message"));
        assertEquals(1, result.get("activationCount"));
        assertEquals(maxActivations, result.get("maxActivations"));
        verify(licenseRepository, times(1)).findByLicenseKey(licenseKey);
        verify(licenseRepository, times(0)).save(any()); // Should not save again
    }

    @Test
    void deactivateLicense_ShouldDeactivateAndReturnSuccess_WhenLicenseIsValid() {
        // Arrange
        testLicense.getHardwareIds().add(hardwareId);
        when(licenseRepository.findByLicenseKey(licenseKey)).thenReturn(Optional.of(testLicense));
        when(licenseRepository.save(any(License.class))).thenReturn(testLicense);

        // Act
        Map<String, Object> result = licenseService.deactivateLicense(licenseKey, hardwareId);

        // Assert
        assertTrue((Boolean) result.get("success"));
        assertEquals("License successfully deactivated", result.get("message"));
        assertEquals(0, result.get("activationCount"));
        assertEquals(maxActivations, result.get("maxActivations"));
        assertFalse(testLicense.getHardwareIds().contains(hardwareId));
        verify(licenseRepository, times(1)).findByLicenseKey(licenseKey);
        verify(licenseRepository, times(1)).save(testLicense);
    }

    @Test
    void deactivateLicense_ShouldReturnError_WhenHardwareIdNotActivated() {
        // Arrange
        when(licenseRepository.findByLicenseKey(licenseKey)).thenReturn(Optional.of(testLicense));

        // Act
        Map<String, Object> result = licenseService.deactivateLicense(licenseKey, hardwareId);

        // Assert
        assertFalse((Boolean) result.get("success"));
        assertEquals("Hardware ID is not activated for this license", result.get("message"));
        assertEquals("HARDWARE_NOT_ACTIVATED", result.get("errorCode"));
        verify(licenseRepository, times(1)).findByLicenseKey(licenseKey);
        verify(licenseRepository, times(0)).save(any()); // Should not save
    }

    @Test
    void revokeLicense_ShouldRevokeAndReturnTrue_WhenLicenseExists() {
        // Arrange
        when(licenseRepository.findById(licenseId)).thenReturn(Optional.of(testLicense));
        when(licenseRepository.save(any(License.class))).thenReturn(testLicense);

        // Act
        boolean result = licenseService.revokeLicense(licenseId);

        // Assert
        assertTrue(result);
        assertTrue(testLicense.isRevoked());
        verify(licenseRepository, times(1)).findById(licenseId);
        verify(licenseRepository, times(1)).save(testLicense);
    }

    @Test
    void revokeLicense_ShouldReturnFalse_WhenLicenseDoesNotExist() {
        // Arrange
        when(licenseRepository.findById(licenseId)).thenReturn(Optional.empty());

        // Act
        boolean result = licenseService.revokeLicense(licenseId);

        // Assert
        assertFalse(result);
        verify(licenseRepository, times(1)).findById(licenseId);
        verify(licenseRepository, times(0)).save(any());
    }

    @Test
    void reinstateRevokedLicense_ShouldReinstateAndReturnTrue_WhenLicenseExists() {
        // Arrange
        testLicense.setRevoked(true);
        when(licenseRepository.findById(licenseId)).thenReturn(Optional.of(testLicense));
        when(licenseRepository.save(any(License.class))).thenReturn(testLicense);

        // Act
        boolean result = licenseService.reinstateRevokedLicense(licenseId);

        // Assert
        assertTrue(result);
        assertFalse(testLicense.isRevoked());
        verify(licenseRepository, times(1)).findById(licenseId);
        verify(licenseRepository, times(1)).save(testLicense);
    }

    @Test
    void generateLicenseKey_ShouldGenerateUniqueKey() {
        // Arrange
        when(licenseRepository.existsByLicenseKey(any())).thenReturn(false);

        // Act
        String result = licenseService.generateLicenseKey("TEST");

        // Assert
        assertNotNull(result);
        assertTrue(result.startsWith("TEST-"));
        assertEquals(24, result.length()); // "TEST-XXXX-XXXX-XXXX-XXXX" is 24 chars
        verify(licenseRepository, times(1)).existsByLicenseKey(any());
    }

    @Test
    void generateLicenseKey_ShouldRegenerateIfKeyExists() {
        // Arrange
        // First check returns true (key exists), second check returns false (new key doesn't exist)
        when(licenseRepository.existsByLicenseKey(any()))
                .thenReturn(true)
                .thenReturn(false);

        // Act
        String result = licenseService.generateLicenseKey("TEST");

        // Assert
        assertNotNull(result);
        assertTrue(result.startsWith("TEST-"));
        assertEquals(24, result.length());
        verify(licenseRepository, times(2)).existsByLicenseKey(any());
    }
}