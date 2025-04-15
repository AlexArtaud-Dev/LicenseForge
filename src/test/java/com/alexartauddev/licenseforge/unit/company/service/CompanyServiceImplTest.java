package com.alexartauddev.licenseforge.unit.company.service;

import com.alexartauddev.licenseforge.application.company.mapper.CompanyMapper;
import com.alexartauddev.licenseforge.application.company.service.impl.CompanyServiceImpl;
import com.alexartauddev.licenseforge.domain.company.entity.Company;
import com.alexartauddev.licenseforge.domain.company.repository.CompanyRepository;
import com.alexartauddev.licenseforge.domain.realm.repository.RealmRepository;
import com.alexartauddev.licenseforge.domain.user.repository.UserRepository;
import com.alexartauddev.licenseforge.web.dto.company.CompanyDTO;
import com.alexartauddev.licenseforge.web.exception.company.CompanyNotFoundException;
import com.alexartauddev.licenseforge.web.exception.company.DuplicateRealmIdException;
import com.alexartauddev.licenseforge.web.request.company.CreateCompanyRequest;
import com.alexartauddev.licenseforge.web.request.company.UpdateCompanyRequest;
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
class CompanyServiceImplTest {

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private RealmRepository realmRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CompanyMapper companyMapper;

    @InjectMocks
    private CompanyServiceImpl companyService;

    private UUID companyId;
    private String realmId;
    private Company company;
    private CompanyDTO companyDTO;
    private CreateCompanyRequest createCompanyRequest;

    @BeforeEach
    void setUp() {
        companyId = UUID.randomUUID();
        realmId = "test-company-1234";

        company = Company.builder()
                .id(companyId)
                .name("Test Company")
                .realmId(realmId)
                .quotaApps(5)
                .quotaKeysPerApp(10)
                .planType(Company.PlanType.FREE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        companyDTO = CompanyDTO.builder()
                .id(companyId)
                .name("Test Company")
                .realmId(realmId)
                .quotaApps(5)
                .quotaKeysPerApp(10)
                .planType(Company.PlanType.FREE)
                .realmsCount(1)
                .usersCount(2)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        createCompanyRequest = CreateCompanyRequest.builder()
                .name("Test Company")
                .realmId(realmId)
                .quotaApps(5)
                .quotaKeysPerApp(10)
                .planType(Company.PlanType.FREE)
                .build();
    }

    @Test
    void createCompany_ValidRequest_ShouldCreateCompany() {
        // Arrange
        when(companyRepository.existsByRealmId(realmId)).thenReturn(false);
        when(companyRepository.save(any(Company.class))).thenReturn(company);
        when(companyMapper.toDTO(any(Company.class))).thenReturn(companyDTO);

        // Act
        CompanyDTO result = companyService.createCompany(createCompanyRequest);

        // Assert
        assertNotNull(result);
        assertEquals(companyDTO, result);
        verify(companyRepository).existsByRealmId(realmId);
        verify(companyRepository).save(any(Company.class));
        verify(companyMapper).toDTO(any(Company.class));
    }

    @Test
    void createCompany_DuplicateRealmId_ShouldThrowException() {
        // Arrange
        when(companyRepository.existsByRealmId(realmId)).thenReturn(true);

        // Act & Assert
        assertThrows(DuplicateRealmIdException.class, () -> companyService.createCompany(createCompanyRequest));
        verify(companyRepository).existsByRealmId(realmId);
        verify(companyRepository, never()).save(any(Company.class));
    }

    @Test
    void getCompanyById_ExistingCompany_ShouldReturnCompany() {
        // Arrange
        when(companyRepository.findById(companyId)).thenReturn(Optional.of(company));
        when(companyMapper.toDTO(company)).thenReturn(companyDTO);
        when(realmRepository.countByCompanyId(companyId)).thenReturn(1L);
        when(userRepository.countByCompanyId(companyId)).thenReturn(2L);

        // Act
        CompanyDTO result = companyService.getCompanyById(companyId);

        // Assert
        assertNotNull(result);
        assertEquals(companyDTO, result);
        verify(companyRepository).findById(companyId);
        verify(companyMapper).toDTO(company);
        verify(realmRepository).countByCompanyId(companyId);
        verify(userRepository).countByCompanyId(companyId);
    }

    @Test
    void getCompanyById_NonExistingCompany_ShouldThrowException() {
        // Arrange
        when(companyRepository.findById(companyId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(CompanyNotFoundException.class, () -> companyService.getCompanyById(companyId));
        verify(companyRepository).findById(companyId);
        verify(companyMapper, never()).toDTO(any(Company.class));
    }

    @Test
    void getCompanyByRealmId_ExistingCompany_ShouldReturnCompany() {
        // Arrange
        when(companyRepository.findByRealmId(realmId)).thenReturn(Optional.of(company));
        when(companyMapper.toDTO(company)).thenReturn(companyDTO);
        when(realmRepository.countByCompanyId(companyId)).thenReturn(1L);
        when(userRepository.countByCompanyId(companyId)).thenReturn(2L);

        // Act
        CompanyDTO result = companyService.getCompanyByRealmId(realmId);

        // Assert
        assertNotNull(result);
        assertEquals(companyDTO, result);
        verify(companyRepository).findByRealmId(realmId);
        verify(companyMapper).toDTO(company);
        verify(realmRepository).countByCompanyId(companyId);
        verify(userRepository).countByCompanyId(companyId);
    }

    @Test
    void getCompanyByRealmId_NonExistingCompany_ShouldThrowException() {
        // Arrange
        when(companyRepository.findByRealmId(realmId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(CompanyNotFoundException.class, () -> companyService.getCompanyByRealmId(realmId));
        verify(companyRepository).findByRealmId(realmId);
        verify(companyMapper, never()).toDTO(any(Company.class));
    }

    @Test
    void getAllCompanies_ShouldReturnAllCompanies() {
        // Arrange
        Company company2 = Company.builder()
                .id(UUID.randomUUID())
                .name("Another Company")
                .realmId("another-company-1234")
                .build();

        List<Company> companies = Arrays.asList(company, company2);

        when(companyRepository.findAll()).thenReturn(companies);
        when(companyMapper.toDTO(any(Company.class))).thenReturn(companyDTO);
        when(realmRepository.countByCompanyId(any(UUID.class))).thenReturn(1L);
        when(userRepository.countByCompanyId(any(UUID.class))).thenReturn(2L);

        // Act
        List<CompanyDTO> result = companyService.getAllCompanies(0, 10);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(companyRepository).findAll();
        verify(companyMapper, times(2)).toDTO(any(Company.class));
        verify(realmRepository, times(2)).countByCompanyId(any(UUID.class));
        verify(userRepository, times(2)).countByCompanyId(any(UUID.class));
    }

    @Test
    void getCompaniesByPlanType_ShouldReturnFilteredCompanies() {
        // Arrange
        List<Company> freeCompanies = Arrays.asList(company);

        when(companyRepository.findByPlanType(Company.PlanType.FREE)).thenReturn(freeCompanies);
        when(companyMapper.toDTO(any(Company.class))).thenReturn(companyDTO);
        when(realmRepository.countByCompanyId(any(UUID.class))).thenReturn(1L);
        when(userRepository.countByCompanyId(any(UUID.class))).thenReturn(2L);

        // Act
        List<CompanyDTO> result = companyService.getCompaniesByPlanType(Company.PlanType.FREE, 0, 10);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(companyDTO, result.get(0));
        verify(companyRepository).findByPlanType(Company.PlanType.FREE);
        verify(companyMapper).toDTO(any(Company.class));
        verify(realmRepository).countByCompanyId(any(UUID.class));
        verify(userRepository).countByCompanyId(any(UUID.class));
    }

    @Test
    void updateCompany_ExistingCompany_ShouldUpdateCompany() {
        // Arrange
        UpdateCompanyRequest request = UpdateCompanyRequest.builder()
                .name("Updated Company")
                .quotaApps(10)
                .quotaKeysPerApp(20)
                .planType(Company.PlanType.BUSINESS)
                .build();

        when(companyRepository.findById(companyId)).thenReturn(Optional.of(company));
        when(companyRepository.save(any(Company.class))).thenReturn(company);
        when(companyMapper.toDTO(company)).thenReturn(companyDTO);
        when(realmRepository.countByCompanyId(companyId)).thenReturn(1L);
        when(userRepository.countByCompanyId(companyId)).thenReturn(2L);

        // Act
        CompanyDTO result = companyService.updateCompany(companyId, request);

        // Assert
        assertNotNull(result);
        assertEquals(companyDTO, result);
        verify(companyRepository).findById(companyId);
        verify(companyRepository).save(company);
        verify(companyMapper).toDTO(company);
        verify(realmRepository).countByCompanyId(companyId);
        verify(userRepository).countByCompanyId(companyId);

        // Verify that company properties were updated
        assertEquals("Updated Company", company.getName());
        assertEquals(10, company.getQuotaApps());
        assertEquals(20, company.getQuotaKeysPerApp());
        assertEquals(Company.PlanType.BUSINESS, company.getPlanType());
    }

    @Test
    void updateCompany_NonExistingCompany_ShouldThrowException() {
        // Arrange
        UpdateCompanyRequest request = new UpdateCompanyRequest();
        when(companyRepository.findById(companyId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(CompanyNotFoundException.class, () -> companyService.updateCompany(companyId, request));
        verify(companyRepository).findById(companyId);
        verify(companyRepository, never()).save(any(Company.class));
    }

    @Test
    void deleteCompany_ExistingCompany_ShouldDeleteCompany() {
        // Arrange
        when(companyRepository.findById(companyId)).thenReturn(Optional.of(company));

        // Act
        companyService.deleteCompany(companyId);

        // Assert
        verify(companyRepository).findById(companyId);
        verify(companyRepository).delete(company);
    }

    @Test
    void deleteCompany_NonExistingCompany_ShouldThrowException() {
        // Arrange
        when(companyRepository.findById(companyId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(CompanyNotFoundException.class, () -> companyService.deleteCompany(companyId));
        verify(companyRepository).findById(companyId);
        verify(companyRepository, never()).delete(any(Company.class));
    }

    @Test
    void countCompanies_ShouldReturnCount() {
        // Arrange
        List<Company> companies = Arrays.asList(company, Company.builder().build());
        when(companyRepository.findAll()).thenReturn(companies);

        // Act
        long result = companyService.countCompanies();

        // Assert
        assertEquals(2, result);
        verify(companyRepository).findAll();
    }

    @Test
    void countByPlanType_ShouldReturnCount() {
        // Arrange
        List<Company> freeCompanies = Arrays.asList(company);
        when(companyRepository.findByPlanType(Company.PlanType.FREE)).thenReturn(freeCompanies);

        // Act
        long result = companyService.countByPlanType(Company.PlanType.FREE);

        // Assert
        assertEquals(1, result);
        verify(companyRepository).findByPlanType(Company.PlanType.FREE);
    }
}