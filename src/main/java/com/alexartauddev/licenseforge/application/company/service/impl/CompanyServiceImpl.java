package com.alexartauddev.licenseforge.application.company.service.impl;

import com.alexartauddev.licenseforge.application.company.mapper.CompanyMapper;
import com.alexartauddev.licenseforge.application.company.service.CompanyService;
import com.alexartauddev.licenseforge.domain.company.entity.Company;
import com.alexartauddev.licenseforge.domain.company.repository.CompanyRepository;
import com.alexartauddev.licenseforge.domain.realm.repository.RealmRepository;
import com.alexartauddev.licenseforge.domain.user.repository.UserRepository;
import com.alexartauddev.licenseforge.web.dto.company.CompanyDTO;
import com.alexartauddev.licenseforge.web.exception.company.CompanyNotFoundException;
import com.alexartauddev.licenseforge.web.exception.company.DuplicateRealmIdException;
import com.alexartauddev.licenseforge.web.request.company.CreateCompanyRequest;
import com.alexartauddev.licenseforge.web.request.company.UpdateCompanyRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository companyRepository;
    private final RealmRepository realmRepository;
    private final UserRepository userRepository;
    private final CompanyMapper companyMapper;

    @Override
    @Transactional
    public CompanyDTO createCompany(CreateCompanyRequest request) {
        // Check if realm ID is already in use
        if (companyRepository.existsByRealmId(request.getRealmId())) {
            throw new DuplicateRealmIdException(request.getRealmId());
        }

        Company company = Company.builder()
                .name(request.getName()) // Make sure this matches your CreateCompanyRequest field
                .realmId(request.getRealmId())
                .quotaApps(request.getQuotaApps())
                .quotaKeysPerApp(request.getQuotaKeysPerApp())
                .planType(request.getPlanType())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Company savedCompany = companyRepository.save(company);

        CompanyDTO dto = companyMapper.toDTO(savedCompany);
        dto.setRealmsCount(0); // New company has no realms
        dto.setUsersCount(0);  // New company has no users

        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public CompanyDTO getCompanyById(UUID id) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> CompanyNotFoundException.withId(id));

        CompanyDTO dto = companyMapper.toDTO(company);
        dto.setRealmsCount(realmRepository.countByCompanyId(company.getId()));
        dto.setUsersCount(userRepository.countByCompanyId(company.getId()));

        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public CompanyDTO getCompanyByRealmId(String realmId) {
        Company company = companyRepository.findByRealmId(realmId)
                .orElseThrow(() -> new CompanyNotFoundException("Company not found with realmId: " + realmId));

        CompanyDTO dto = companyMapper.toDTO(company);
        dto.setRealmsCount(realmRepository.countByCompanyId(company.getId()));
        dto.setUsersCount(userRepository.countByCompanyId(company.getId()));

        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CompanyDTO> getAllCompanies(int page, int size) {
        return companyRepository.findAll().stream()
                .skip((long) page * size)
                .limit(size)
                .map(company -> {
                    CompanyDTO dto = companyMapper.toDTO(company);
                    dto.setRealmsCount(realmRepository.countByCompanyId(company.getId()));
                    dto.setUsersCount(userRepository.countByCompanyId(company.getId()));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CompanyDTO> getCompaniesByPlanType(Company.PlanType planType, int page, int size) {
        return companyRepository.findByPlanType(planType).stream()
                .skip((long) page * size)
                .limit(size)
                .map(company -> {
                    CompanyDTO dto = companyMapper.toDTO(company);
                    dto.setRealmsCount(realmRepository.countByCompanyId(company.getId()));
                    dto.setUsersCount(userRepository.countByCompanyId(company.getId()));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CompanyDTO updateCompany(UUID id, UpdateCompanyRequest request) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> CompanyNotFoundException.withId(id));

        if (request.getName() != null) {
            company.setName(request.getName());
        }

        if (request.getQuotaApps() != null) {
            company.setQuotaApps(request.getQuotaApps());
        }

        if (request.getQuotaKeysPerApp() != null) {
            company.setQuotaKeysPerApp(request.getQuotaKeysPerApp());
        }

        if (request.getPlanType() != null) {
            company.setPlanType(request.getPlanType());
        }

        company.setUpdatedAt(LocalDateTime.now());

        Company updatedCompany = companyRepository.save(company);

        CompanyDTO dto = companyMapper.toDTO(updatedCompany);
        dto.setRealmsCount(realmRepository.countByCompanyId(updatedCompany.getId()));
        dto.setUsersCount(userRepository.countByCompanyId(updatedCompany.getId()));

        return dto;
    }

    @Override
    @Transactional
    public void deleteCompany(UUID id) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> CompanyNotFoundException.withId(id));

        companyRepository.delete(company);
    }

    @Override
    @Transactional(readOnly = true)
    public long countCompanies() {
        return companyRepository.findAll().size();
    }

    @Override
    @Transactional(readOnly = true)
    public long countByPlanType(Company.PlanType planType) {
        return companyRepository.findByPlanType(planType).size();
    }
}