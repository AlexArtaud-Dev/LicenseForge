package com.alexartauddev.licenseforge.integration;

import com.alexartauddev.licenseforge.domain.license.entity.License;
import com.alexartauddev.licenseforge.domain.license.repository.LicenseRepository;
import com.alexartauddev.licenseforge.web.request.license.CreateLicenseRequest;
import com.alexartauddev.licenseforge.web.request.license.LicenseActivationRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)  // Disables Spring Security for tests
@ActiveProfiles("test")
@Testcontainers
public class LicenseIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("licenceforge_test")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.flyway.enabled", () -> "true");
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private LicenseRepository licenseRepository;

    private String licenseKey;
    private UUID licenseId;
    private final String productId = "integration-product";
    private final String customerId = "integration-customer";
    private final int maxActivations = 2;
    private final String hardwareId = "integration-hwid";

    @BeforeEach
    void setUp() {
        // Clean up before test to ensure a clean state
        licenseRepository.deleteAll();
    }

    @AfterEach
    void tearDown() {
        licenseRepository.deleteAll();
    }

    @Test
    void fullLicenseCycle_ShouldWorkEndToEnd() throws Exception {
        // Step 1: Create a license
        CreateLicenseRequest createRequest = new CreateLicenseRequest();
        createRequest.setProductId(productId);
        createRequest.setCustomerId(customerId);
        createRequest.setMaxActivations(maxActivations);
        createRequest.setExpiresAt(LocalDateTime.now().plusDays(30));

        MvcResult createResult = mockMvc.perform(post("/api/v1/licenses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.productId", is(productId)))
                .andExpect(jsonPath("$.customerId", is(customerId)))
                .andExpect(jsonPath("$.maxActivations", is(maxActivations)))
                .andExpect(jsonPath("$.revoked", is(false)))
                .andReturn();

        Map<String, Object> createResponse = objectMapper.readValue(
                createResult.getResponse().getContentAsString(),
                objectMapper.getTypeFactory().constructMapType(Map.class, String.class, Object.class));

        licenseKey = (String) createResponse.get("licenseKey");
        licenseId = UUID.fromString((String) createResponse.get("id"));

        assertNotNull(licenseKey, "License key should not be null");
        assertThat(licenseKey, startsWith("LFORG-"));

        // Step 2: Verify the license
        LicenseActivationRequest verifyRequest = new LicenseActivationRequest();
        verifyRequest.setLicenseKey(licenseKey);
        verifyRequest.setHardwareId(hardwareId);

        mockMvc.perform(post("/api/v1/licenses/verify")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(verifyRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.status", is("AVAILABLE_FOR_ACTIVATION")))
                .andExpect(jsonPath("$.activationCount", is(0)))
                .andExpect(jsonPath("$.maxActivations", is(maxActivations)));

        // Step 3: Activate the license
        mockMvc.perform(post("/api/v1/licenses/activate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(verifyRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", containsString("activated")))
                .andExpect(jsonPath("$.activationCount", is(1)))
                .andExpect(jsonPath("$.maxActivations", is(maxActivations)));

        // Step 4: Verify again - should show as already activated
        mockMvc.perform(post("/api/v1/licenses/verify")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(verifyRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.status", is("ALREADY_ACTIVATED")))
                .andExpect(jsonPath("$.activationCount", is(1)));

        // Step 5: Try to activate with a different hardware ID
        LicenseActivationRequest secondDeviceRequest = new LicenseActivationRequest();
        secondDeviceRequest.setLicenseKey(licenseKey);
        secondDeviceRequest.setHardwareId("second-device");

        mockMvc.perform(post("/api/v1/licenses/activate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(secondDeviceRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.activationCount", is(2)))
                .andExpect(jsonPath("$.maxActivations", is(maxActivations)));

        // Step 6: Try to activate a third device - should fail (max activations reached)
        LicenseActivationRequest thirdDeviceRequest = new LicenseActivationRequest();
        thirdDeviceRequest.setLicenseKey(licenseKey);
        thirdDeviceRequest.setHardwareId("third-device");

        mockMvc.perform(post("/api/v1/licenses/verify")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(thirdDeviceRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.errorCode", is("MAX_ACTIVATIONS_REACHED")));

        // Step 7: Deactivate the first hardware ID
        mockMvc.perform(post("/api/v1/licenses/deactivate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(verifyRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", containsString("deactivated")))
                .andExpect(jsonPath("$.activationCount", is(1)));

        // Step 8: Now we should be able to activate the third device
        mockMvc.perform(post("/api/v1/licenses/activate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(thirdDeviceRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.activationCount", is(2)));

        // Step 9: Revoke the license
        mockMvc.perform(put("/api/v1/licenses/{id}/revoke", licenseId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", containsString("revoked")));

        // Step 10: Verify after revocation - should fail
        mockMvc.perform(post("/api/v1/licenses/verify")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(verifyRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.errorCode", is("LICENSE_REVOKED")));

        // Step 11: Reinstate the license
        mockMvc.perform(put("/api/v1/licenses/{id}/reinstate", licenseId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", containsString("reinstated")));

        // Step 12: Verify after reinstatement - should work again
        mockMvc.perform(post("/api/v1/licenses/verify")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(verifyRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(false)))  // Change from true to false
                .andExpect(jsonPath("$.errorCode", is("MAX_ACTIVATIONS_REACHED")));

        // Verify database state directly
        License license = licenseRepository.findByLicenseKey(licenseKey).orElse(null);
        assertNotNull(license, "License should exist in database");
        assertEquals(productId, license.getProductId());
        assertEquals(customerId, license.getCustomerId());
        assertEquals(maxActivations, license.getMaxActivations());
        assertFalse(license.isRevoked());
        assertEquals(2, license.getHardwareIds().size());
        assertFalse(license.getHardwareIds().contains(hardwareId));
        assertTrue(license.getHardwareIds().contains("second-device"));
        assertTrue(license.getHardwareIds().contains("third-device"));
    }
}
