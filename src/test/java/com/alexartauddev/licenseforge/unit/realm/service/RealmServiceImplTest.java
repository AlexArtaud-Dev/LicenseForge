package com.alexartauddev.licenseforge.unit.realm.service;

import com.alexartauddev.licenseforge.application.realm.mapper.RealmMapper;
import com.alexartauddev.licenseforge.application.realm.service.impl.RealmServiceImpl;
import com.alexartauddev.licenseforge.domain.application.repository.ApplicationRepository;
import com.alexartauddev.licenseforge.domain.realm.entity.Realm;
import com.alexartauddev.licenseforge.domain.realm.repository.RealmRepository;
import com.alexartauddev.licenseforge.web.dto.realm.RealmDTO;
import com.alexartauddev.licenseforge.web.exception.realm.RealmNotFoundException;
import com.alexartauddev.licenseforge.web.request.realm.CreateRealmRequest;
import com.alexartauddev.licenseforge.web.request.realm.UpdateRealmRequest;
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
class RealmServiceImplTest {

    @Mock
    private RealmRepository realmRepository;

    @Mock
    private ApplicationRepository applicationRepository;

    @Mock
    private RealmMapper realmMapper;

    @InjectMocks
    private RealmServiceImpl realmService;

    private UUID realmId;
    private UUID companyId;
    private Realm realm;
    private RealmDTO realmDTO;
    private CreateRealmRequest createRealmRequest;

    @BeforeEach
    void setUp() {
        realmId = UUID.randomUUID();
        companyId = UUID.randomUUID();

        realm = Realm.builder()
                .id(realmId)
                .name("Test Realm")
                .description("Test Realm Description")
                .companyId(companyId)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        realmDTO = RealmDTO.builder()
                .id(realmId)
                .name("Test Realm")
                .description("Test Realm Description")
                .companyId(companyId)
                .applicationsCount(3)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        createRealmRequest = CreateRealmRequest.builder()
                .name("Test Realm")
                .description("Test Realm Description")
                .companyId(companyId)
                .build();
    }

    @Test
    void createRealm_ValidRequest_ShouldCreateRealm() {
        // Arrange
        when(realmRepository.save(any(Realm.class))).thenReturn(realm);
        when(realmMapper.toDTO(any(Realm.class))).thenReturn(realmDTO);

        // Act
        RealmDTO result = realmService.createRealm(createRealmRequest);

        // Assert
        assertNotNull(result);
        assertEquals(realmDTO, result);
        verify(realmRepository).save(any(Realm.class));
        verify(realmMapper).toDTO(any(Realm.class));
    }

    @Test
    void getRealmById_ExistingRealm_ShouldReturnRealm() {
        // Arrange
        when(realmRepository.findById(realmId)).thenReturn(Optional.of(realm));
        when(realmMapper.toDTO(realm)).thenReturn(realmDTO);
        when(applicationRepository.countByRealmId(realmId)).thenReturn(3L);

        // Act
        RealmDTO result = realmService.getRealmById(realmId);

        // Assert
        assertNotNull(result);
        assertEquals(realmDTO, result);
        verify(realmRepository).findById(realmId);
        verify(realmMapper).toDTO(realm);
        verify(applicationRepository).countByRealmId(realmId);
    }

    @Test
    void getRealmById_NonExistingRealm_ShouldThrowException() {
        // Arrange
        when(realmRepository.findById(realmId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RealmNotFoundException.class, () -> realmService.getRealmById(realmId));
        verify(realmRepository).findById(realmId);
        verify(realmMapper, never()).toDTO(any(Realm.class));
    }

    @Test
    void getRealmsByCompanyId_ShouldReturnRealms() {
        // Arrange
        List<Realm> realms = Arrays.asList(realm, realm);
        when(realmRepository.findByCompanyId(companyId)).thenReturn(realms);
        when(realmMapper.toDTO(any(Realm.class))).thenReturn(realmDTO);
        when(applicationRepository.countByRealmId(any(UUID.class))).thenReturn(3L);

        // Act
        List<RealmDTO> result = realmService.getRealmsByCompanyId(companyId, 0, 10);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(realmDTO, result.get(0));
        verify(realmRepository).findByCompanyId(companyId);
        verify(realmMapper, times(2)).toDTO(any(Realm.class));
        verify(applicationRepository, times(2)).countByRealmId(any(UUID.class));
    }

    @Test
    void updateRealm_ExistingRealm_ShouldUpdateRealm() {
        // Arrange
        UpdateRealmRequest request = UpdateRealmRequest.builder()
                .name("Updated Realm")
                .description("Updated Description")
                .build();

        when(realmRepository.findById(realmId)).thenReturn(Optional.of(realm));
        when(realmRepository.save(any(Realm.class))).thenReturn(realm);
        when(realmMapper.toDTO(realm)).thenReturn(realmDTO);
        when(applicationRepository.countByRealmId(realmId)).thenReturn(3L);

        // Act
        RealmDTO result = realmService.updateRealm(realmId, request);

        // Assert
        assertNotNull(result);
        assertEquals(realmDTO, result);
        verify(realmRepository).findById(realmId);
        verify(realmRepository).save(realm);
        verify(realmMapper).toDTO(realm);
        verify(applicationRepository).countByRealmId(realmId);

        // Verify that the realm was updated with the new values
        assertEquals("Updated Realm", realm.getName());
        assertEquals("Updated Description", realm.getDescription());
    }

    @Test
    void updateRealm_NonExistingRealm_ShouldThrowException() {
        // Arrange
        UpdateRealmRequest request = new UpdateRealmRequest();
        when(realmRepository.findById(realmId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RealmNotFoundException.class, () -> realmService.updateRealm(realmId, request));
        verify(realmRepository).findById(realmId);
        verify(realmRepository, never()).save(any(Realm.class));
    }

    @Test
    void deleteRealm_ExistingRealm_ShouldDeleteRealm() {
        // Arrange
        when(realmRepository.findById(realmId)).thenReturn(Optional.of(realm));
        doNothing().when(realmRepository).delete(realm);

        // Act
        realmService.deleteRealm(realmId);

        // Assert
        verify(realmRepository).findById(realmId);
        verify(realmRepository).delete(realm);
    }

    @Test
    void deleteRealm_NonExistingRealm_ShouldThrowException() {
        // Arrange
        when(realmRepository.findById(realmId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RealmNotFoundException.class, () -> realmService.deleteRealm(realmId));
        verify(realmRepository).findById(realmId);
        verify(realmRepository, never()).delete(any(Realm.class));
    }

    @Test
    void countByCompanyId_ShouldReturnCount() {
        // Arrange
        when(realmRepository.countByCompanyId(companyId)).thenReturn(5L);

        // Act
        long result = realmService.countByCompanyId(companyId);

        // Assert
        assertEquals(5L, result);
        verify(realmRepository).countByCompanyId(companyId);
    }
}