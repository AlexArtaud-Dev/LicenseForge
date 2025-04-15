package com.alexartauddev.licenseforge.unit.application.service;

import com.alexartauddev.licenseforge.application.license.mapper.LicenseMapper;
import com.alexartauddev.licenseforge.application.license.service.impl.ActivationServiceImpl;
import com.alexartauddev.licenseforge.domain.license.entity.Activation;
import com.alexartauddev.licenseforge.domain.license.repository.ActivationRepository;
import com.alexartauddev.licenseforge.web.dto.license.ActivationDTO;
import com.alexartauddev.licenseforge.web.exception.license.ActivationNotFoundException;
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
    private ActivationRepository activationRepository;

    @Mock
    private LicenseMapper licenseMapper;

    @InjectMocks
    private ActivationServiceImpl activationService;

    private UUID activationId;
    private UUID licenseId;
    private String hardwareId;
    private Activation activation;
    private ActivationDTO activationDTO;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        activationId = UUID.randomUUID();
        licenseId = UUID.randomUUID();
        hardwareId = "HARDWARE-ABC-123";
        now = LocalDateTime.now();

        activation = Activation.builder()
                .id(activationId)
                .licenseId(licenseId)
                .hardwareId(hardwareId)
                .activatedAt(now.minusDays(10))
                .lastSeenAt(now.minusDays(2))
                .build();

        activationDTO = ActivationDTO.builder()
                .id(activationId)
                .licenseId(licenseId)
                .hardwareId(hardwareId)
                .activatedAt(now.minusDays(10))
                .lastSeenAt(now.minusDays(2))
                .build();
    }

    @Test
    void getActivationsByLicenseId_ShouldReturnActivations() {
        // Arrange
        List<Activation> activations = Arrays.asList(activation, activation);
        when(activationRepository.findByLicenseId(licenseId)).thenReturn(activations);
        when(licenseMapper.toDTO(any(Activation.class))).thenReturn(activationDTO);

        // Act
        List<ActivationDTO> result = activationService.getActivationsByLicenseId(licenseId, 0, 10);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(activationDTO, result.get(0));
        verify(activationRepository).findByLicenseId(licenseId);
        verify(licenseMapper, times(2)).toDTO(any(Activation.class));
    }

    @Test
    void getActivationById_ExistingActivation_ShouldReturnActivation() {
        // Arrange
        when(activationRepository.findById(activationId)).thenReturn(Optional.of(activation));
        when(licenseMapper.toDTO(activation)).thenReturn(activationDTO);

        // Act
        ActivationDTO result = activationService.getActivationById(activationId);

        // Assert
        assertNotNull(result);
        assertEquals(activationDTO, result);
        verify(activationRepository).findById(activationId);
        verify(licenseMapper).toDTO(activation);
    }

    @Test
    void getActivationById_NonExistingActivation_ShouldThrowException() {
        // Arrange
        when(activationRepository.findById(activationId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ActivationNotFoundException.class, () -> activationService.getActivationById(activationId));
        verify(activationRepository).findById(activationId);
        verify(licenseMapper, never()).toDTO(any(Activation.class));
    }

    @Test
    void getActivationByLicenseIdAndHardwareId_ExistingActivation_ShouldReturnActivation() {
        // Arrange
        when(activationRepository.findByLicenseIdAndHardwareId(licenseId, hardwareId))
                .thenReturn(Optional.of(activation));
        when(licenseMapper.toDTO(activation)).thenReturn(activationDTO);

        // Act
        ActivationDTO result = activationService.getActivationByLicenseIdAndHardwareId(licenseId, hardwareId);

        // Assert
        assertNotNull(result);
        assertEquals(activationDTO, result);
        verify(activationRepository).findByLicenseIdAndHardwareId(licenseId, hardwareId);
        verify(licenseMapper).toDTO(activation);
    }

    @Test
    void getActivationByLicenseIdAndHardwareId_NonExistingActivation_ShouldThrowException() {
        // Arrange
        when(activationRepository.findByLicenseIdAndHardwareId(licenseId, hardwareId))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ActivationNotFoundException.class,
                () -> activationService.getActivationByLicenseIdAndHardwareId(licenseId, hardwareId));
        verify(activationRepository).findByLicenseIdAndHardwareId(licenseId, hardwareId);
        verify(licenseMapper, never()).toDTO(any(Activation.class));
    }

    @Test
    void updateLastSeen_ExistingActivation_ShouldUpdateLastSeen() {
        // Arrange
        when(activationRepository.findById(activationId)).thenReturn(Optional.of(activation));
        when(activationRepository.save(any(Activation.class))).thenReturn(activation);
        when(licenseMapper.toDTO(activation)).thenReturn(activationDTO);

        // Act
        ActivationDTO result = activationService.updateLastSeen(activationId);

        // Assert
        assertNotNull(result);
        assertEquals(activationDTO, result);
        verify(activationRepository).findById(activationId);
        verify(activationRepository).save(activation);
        verify(licenseMapper).toDTO(activation);

        // Verify that lastSeenAt was updated
        assertTrue(activation.getLastSeenAt().isAfter(now.minusDays(2)));
    }

    @Test
    void updateLastSeen_NonExistingActivation_ShouldThrowException() {
        // Arrange
        when(activationRepository.findById(activationId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ActivationNotFoundException.class, () -> activationService.updateLastSeen(activationId));
        verify(activationRepository).findById(activationId);
        verify(activationRepository, never()).save(any(Activation.class));
    }

    @Test
    void findInactiveActivations_ShouldReturnInactiveActivations() {
        // Arrange
        LocalDateTime threshold = LocalDateTime.now().minusDays(7);
        List<Activation> inactiveActivations = Arrays.asList(activation);

        when(activationRepository.findInactiveActivations(threshold)).thenReturn(inactiveActivations);
        when(licenseMapper.toDTO(any(Activation.class))).thenReturn(activationDTO);

        // Act
        List<ActivationDTO> result = activationService.findInactiveActivations(threshold, 0, 10);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(activationDTO, result.get(0));
        verify(activationRepository).findInactiveActivations(threshold);
        verify(licenseMapper).toDTO(any(Activation.class));
    }

    @Test
    void deleteActivation_ExistingActivation_ShouldDeleteActivation() {
        // Arrange
        when(activationRepository.findById(activationId)).thenReturn(Optional.of(activation));

        // Act
        activationService.deleteActivation(activationId);

        // Assert
        verify(activationRepository).findById(activationId);
        verify(activationRepository).delete(activation);
    }

    @Test
    void deleteActivation_NonExistingActivation_ShouldThrowException() {
        // Arrange
        when(activationRepository.findById(activationId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ActivationNotFoundException.class, () -> activationService.deleteActivation(activationId));
        verify(activationRepository).findById(activationId);
        verify(activationRepository, never()).delete(any(Activation.class));
    }

    @Test
    void countByLicenseId_ShouldReturnCount() {
        // Arrange
        when(activationRepository.countByLicenseId(licenseId)).thenReturn(3L);

        // Act
        long result = activationService.countByLicenseId(licenseId);

        // Assert
        assertEquals(3L, result);
        verify(activationRepository).countByLicenseId(licenseId);
    }
}