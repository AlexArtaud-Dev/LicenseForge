package com.alexartauddev.licenseforge.application.realm.service.impl;

import com.alexartauddev.licenseforge.application.realm.mapper.RealmMapper;
import com.alexartauddev.licenseforge.application.realm.service.RealmService;
import com.alexartauddev.licenseforge.domain.application.repository.ApplicationRepository;
import com.alexartauddev.licenseforge.domain.realm.entity.Realm;
import com.alexartauddev.licenseforge.domain.realm.repository.RealmRepository;
import com.alexartauddev.licenseforge.web.dto.realm.RealmDTO;
import com.alexartauddev.licenseforge.web.exception.realm.RealmNotFoundException;
import com.alexartauddev.licenseforge.web.request.realm.CreateRealmRequest;
import com.alexartauddev.licenseforge.web.request.realm.UpdateRealmRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RealmServiceImpl implements RealmService {

    private final RealmRepository realmRepository;
    private final ApplicationRepository applicationRepository;
    private final RealmMapper realmMapper;

    @Override
    @Transactional
    public RealmDTO createRealm(CreateRealmRequest request) {
        Realm realm = Realm.builder()
                .name(request.getName())
                .description(request.getDescription())
                .companyId(request.getCompanyId())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Realm savedRealm = realmRepository.save(realm);

        RealmDTO dto = realmMapper.toDTO(savedRealm);
        dto.setApplicationsCount(0); // New realm has no applications

        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public RealmDTO getRealmById(UUID id) {
        Realm realm = realmRepository.findById(id)
                .orElseThrow(() -> RealmNotFoundException.withId(id));

        RealmDTO dto = realmMapper.toDTO(realm);
        dto.setApplicationsCount(applicationRepository.countByRealmId(realm.getId()));

        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<RealmDTO> getRealmsByCompanyId(UUID companyId, int page, int size) {
        return realmRepository.findByCompanyId(companyId).stream()
                .skip((long) page * size)
                .limit(size)
                .map(realm -> {
                    RealmDTO dto = realmMapper.toDTO(realm);
                    dto.setApplicationsCount(applicationRepository.countByRealmId(realm.getId()));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public RealmDTO updateRealm(UUID id, UpdateRealmRequest request) {
        Realm realm = realmRepository.findById(id)
                .orElseThrow(() -> RealmNotFoundException.withId(id));

        if (request.getName() != null) {
            realm.setName(request.getName());
        }

        if (request.getDescription() != null) {
            realm.setDescription(request.getDescription());
        }

        realm.setUpdatedAt(LocalDateTime.now());

        Realm updatedRealm = realmRepository.save(realm);

        RealmDTO dto = realmMapper.toDTO(updatedRealm);
        dto.setApplicationsCount(applicationRepository.countByRealmId(updatedRealm.getId()));

        return dto;
    }

    @Override
    @Transactional
    public void deleteRealm(UUID id) {
        Realm realm = realmRepository.findById(id)
                .orElseThrow(() -> RealmNotFoundException.withId(id));

        realmRepository.delete(realm);
    }

    @Override
    @Transactional(readOnly = true)
    public long countByCompanyId(UUID companyId) {
        return realmRepository.countByCompanyId(companyId);
    }
}