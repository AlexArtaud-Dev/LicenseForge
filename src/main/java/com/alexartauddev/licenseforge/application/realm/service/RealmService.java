package com.alexartauddev.licenseforge.application.realm.service;

import com.alexartauddev.licenseforge.web.dto.realm.RealmDTO;
import com.alexartauddev.licenseforge.web.request.realm.CreateRealmRequest;
import com.alexartauddev.licenseforge.web.request.realm.UpdateRealmRequest;

import java.util.List;
import java.util.UUID;

public interface RealmService {
    RealmDTO createRealm(CreateRealmRequest request);

    RealmDTO getRealmById(UUID id);

    List<RealmDTO> getRealmsByCompanyId(UUID companyId, int page, int size);

    RealmDTO updateRealm(UUID id, UpdateRealmRequest request);

    void deleteRealm(UUID id);

    long countByCompanyId(UUID companyId);
}