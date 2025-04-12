package com.alexartauddev.licenseforge.web.controller;

import com.alexartauddev.licenseforge.TestSecurityConfiguration;
import com.alexartauddev.licenseforge.application.service.LicenseService;
import com.alexartauddev.licenseforge.web.dto.license.LicenseDTO;
import com.alexartauddev.licenseforge.web.request.license.CreateLicenseRequest;
import com.alexartauddev.licenseforge.web.request.license.LicenseActivationRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.*;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LicenseController.class)
@Import(TestSecurityConfiguration.class)
class LicenseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private LicenseService licenseService;

    private final UUID licenseId = UUID.randomUUID();
    private final String licenseKey = "LFORG-TEST-1234-5678-9ABC";
    private final String productId = "test-product";
    private final String customerId = "test-customer";
    private final int maxActivations = 3;
    private final String hardwareId = "test-hardware-id";
    private LicenseDTO licenseDTO;

    @TestConfiguration
    static class LicenseControllerTestConfiguration {
        @Bean
        public LicenseService licenseService() {
            return mock(LicenseService.class);
        }
    }

    @BeforeEach
    void setUp() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime futureDate = now.plusMonths(12);

        licenseDTO = LicenseDTO.builder()
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

        // Reset the mock before each test
        reset(licenseService);
    }

    @Test
    void createLicense_ShouldReturnCreatedLicense() throws Exception {
        // Arrange
        CreateLicenseRequest request = new CreateLicenseRequest();
        request.setProductId(productId);
        request.setCustomerId(customerId);
        request.setMaxActivations(maxActivations);
        request.setExpiresAt(LocalDateTime.now().plusMonths(12));

        when(licenseService.createLicense(
                eq(productId),
                eq(customerId),
                eq(maxActivations),
                any(LocalDateTime.class))
        ).thenReturn(licenseDTO);

        // Act & Assert
        mockMvc.perform(post("/api/v1/licenses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(licenseId.toString())))
                .andExpect(jsonPath("$.licenseKey", is(licenseKey)))
                .andExpect(jsonPath("$.productId", is(productId)))
                .andExpect(jsonPath("$.customerId", is(customerId)))
                .andExpect(jsonPath("$.maxActivations", is(maxActivations)))
                .andExpect(jsonPath("$.revoked", is(false)));

        verify(licenseService, times(1)).createLicense(
                eq(productId),
                eq(customerId),
                eq(maxActivations),
                any(LocalDateTime.class));
    }

    @Test
    void getLicense_ShouldReturnLicense_WhenLicenseExists() throws Exception {
        // Arrange
        when(licenseService.getLicense(licenseId)).thenReturn(Optional.of(licenseDTO));

        // Act & Assert
        mockMvc.perform(get("/api/v1/licenses/{id}", licenseId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(licenseId.toString())))
                .andExpect(jsonPath("$.licenseKey", is(licenseKey)));

        verify(licenseService, times(1)).getLicense(licenseId);
    }

    @Test
    void getLicense_ShouldReturnNotFound_WhenLicenseDoesNotExist() throws Exception {
        // Arrange
        when(licenseService.getLicense(licenseId)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/v1/licenses/{id}", licenseId))
                .andExpect(status().isNotFound());

        verify(licenseService, times(1)).getLicense(licenseId);
    }

    @Test
    void getLicenseByKey_ShouldReturnLicense_WhenLicenseExists() throws Exception {
        // Arrange
        when(licenseService.getLicenseByKey(licenseKey)).thenReturn(Optional.of(licenseDTO));

        // Act & Assert
        mockMvc.perform(get("/api/v1/licenses/key/{licenseKey}", licenseKey))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(licenseId.toString())))
                .andExpect(jsonPath("$.licenseKey", is(licenseKey)));

        verify(licenseService, times(1)).getLicenseByKey(licenseKey);
    }

    @Test
    void verifyLicense_ShouldReturnSuccessResponse_WhenLicenseIsValid() throws Exception {
        // Arrange
        LicenseActivationRequest request = new LicenseActivationRequest();
        request.setLicenseKey(licenseKey);
        request.setHardwareId(hardwareId);

        Map<String, Object> serviceResponse = new HashMap<>();
        serviceResponse.put("success", true);
        serviceResponse.put("message", "License is valid and can be activated");
        serviceResponse.put("status", "AVAILABLE_FOR_ACTIVATION");
        serviceResponse.put("activationCount", 0);
        serviceResponse.put("maxActivations", maxActivations);
        serviceResponse.put("expiresAt", licenseDTO.getExpiresAt());

        when(licenseService.verifyLicense(licenseKey, hardwareId)).thenReturn(serviceResponse);

        // Act & Assert
        mockMvc.perform(post("/api/v1/licenses/verify")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", is("License is valid and can be activated")))
                .andExpect(jsonPath("$.status", is("AVAILABLE_FOR_ACTIVATION")))
                .andExpect(jsonPath("$.activationCount", is(0)))
                .andExpect(jsonPath("$.maxActivations", is(maxActivations)));

        verify(licenseService, times(1)).verifyLicense(licenseKey, hardwareId);
    }

    @Test
    void verifyLicense_ShouldReturnErrorResponse_WhenLicenseIsInvalid() throws Exception {
        // Arrange
        LicenseActivationRequest request = new LicenseActivationRequest();
        request.setLicenseKey(licenseKey);
        request.setHardwareId(hardwareId);

        Map<String, Object> serviceResponse = new HashMap<>();
        serviceResponse.put("success", false);
        serviceResponse.put("message", "License is expired");
        serviceResponse.put("errorCode", "LICENSE_EXPIRED");
        serviceResponse.put("expiryDate", licenseDTO.getExpiresAt().minusYears(1));

        when(licenseService.verifyLicense(licenseKey, hardwareId)).thenReturn(serviceResponse);

        // Act & Assert
        mockMvc.perform(post("/api/v1/licenses/verify")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", is("License is expired")))
                .andExpect(jsonPath("$.errorCode", is("LICENSE_EXPIRED")));

        verify(licenseService, times(1)).verifyLicense(licenseKey, hardwareId);
    }

    @Test
    void activateLicense_ShouldReturnSuccessResponse_WhenActivationSucceeds() throws Exception {
        // Arrange
        LicenseActivationRequest request = new LicenseActivationRequest();
        request.setLicenseKey(licenseKey);
        request.setHardwareId(hardwareId);

        Map<String, Object> serviceResponse = new HashMap<>();
        serviceResponse.put("success", true);
        serviceResponse.put("message", "License successfully activated");
        serviceResponse.put("activationCount", 1);
        serviceResponse.put("maxActivations", maxActivations);

        when(licenseService.activateLicense(licenseKey, hardwareId)).thenReturn(serviceResponse);

        // Act & Assert
        mockMvc.perform(post("/api/v1/licenses/activate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", containsString("activated")))
                .andExpect(jsonPath("$.activationCount", is(1)))
                .andExpect(jsonPath("$.maxActivations", is(maxActivations)));

        verify(licenseService, times(1)).activateLicense(licenseKey, hardwareId);
    }

    @Test
    void deactivateLicense_ShouldReturnSuccessResponse_WhenDeactivationSucceeds() throws Exception {
        // Arrange
        LicenseActivationRequest request = new LicenseActivationRequest();
        request.setLicenseKey(licenseKey);
        request.setHardwareId(hardwareId);

        Map<String, Object> serviceResponse = new HashMap<>();
        serviceResponse.put("success", true);
        serviceResponse.put("message", "License successfully deactivated");
        serviceResponse.put("activationCount", 0);
        serviceResponse.put("maxActivations", maxActivations);

        when(licenseService.deactivateLicense(licenseKey, hardwareId)).thenReturn(serviceResponse);

        // Act & Assert
        mockMvc.perform(post("/api/v1/licenses/deactivate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", containsString("deactivated")))
                .andExpect(jsonPath("$.activationCount", is(0)))
                .andExpect(jsonPath("$.maxActivations", is(maxActivations)));

        verify(licenseService, times(1)).deactivateLicense(licenseKey, hardwareId);
    }

    @Test
    void deactivateLicense_ShouldReturnErrorResponse_WhenDeactivationFails() throws Exception {
        // Arrange
        LicenseActivationRequest request = new LicenseActivationRequest();
        request.setLicenseKey(licenseKey);
        request.setHardwareId(hardwareId);

        Map<String, Object> serviceResponse = new HashMap<>();
        serviceResponse.put("success", false);
        serviceResponse.put("message", "Hardware ID is not activated for this license");
        serviceResponse.put("errorCode", "HARDWARE_NOT_ACTIVATED");

        when(licenseService.deactivateLicense(licenseKey, hardwareId)).thenReturn(serviceResponse);

        // Act & Assert
        mockMvc.perform(post("/api/v1/licenses/deactivate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", is("Hardware ID is not activated for this license")))
                .andExpect(jsonPath("$.errorCode", is("HARDWARE_NOT_ACTIVATED")));

        verify(licenseService, times(1)).deactivateLicense(licenseKey, hardwareId);
    }

    @Test
    void revokeLicense_ShouldReturnSuccessResponse_WhenRevocationSucceeds() throws Exception {
        // Arrange
        when(licenseService.revokeLicense(licenseId)).thenReturn(true);

        // Act & Assert
        mockMvc.perform(put("/api/v1/licenses/{id}/revoke", licenseId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", containsString("revoked")));

        verify(licenseService, times(1)).revokeLicense(licenseId);
    }

    @Test
    void revokeLicense_ShouldReturnErrorResponse_WhenRevocationFails() throws Exception {
        // Arrange
        when(licenseService.revokeLicense(licenseId)).thenReturn(false);

        // Act & Assert
        mockMvc.perform(put("/api/v1/licenses/{id}/revoke", licenseId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", containsString("Failed")));

        verify(licenseService, times(1)).revokeLicense(licenseId);
    }

    @Test
    void reinstateLicense_ShouldReturnSuccessResponse_WhenReinstatementSucceeds() throws Exception {
        // Arrange
        when(licenseService.reinstateRevokedLicense(licenseId)).thenReturn(true);

        // Act & Assert
        mockMvc.perform(put("/api/v1/licenses/{id}/reinstate", licenseId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", containsString("reinstated")));

        verify(licenseService, times(1)).reinstateRevokedLicense(licenseId);
    }

    @Test
    void reinstateLicense_ShouldReturnErrorResponse_WhenReinstatementFails() throws Exception {
        // Arrange
        when(licenseService.reinstateRevokedLicense(licenseId)).thenReturn(false);

        // Act & Assert
        mockMvc.perform(put("/api/v1/licenses/{id}/reinstate", licenseId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", containsString("Failed")));

        verify(licenseService, times(1)).reinstateRevokedLicense(licenseId);
    }
}
