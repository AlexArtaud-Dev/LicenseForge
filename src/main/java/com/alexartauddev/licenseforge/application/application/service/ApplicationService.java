package com.alexartauddev.licenseforge.application.application.service;


import com.alexartauddev.licenseforge.web.dto.application.ApplicationDTO;
import com.alexartauddev.licenseforge.web.request.application.CreateApplicationRequest;
import com.alexartauddev.licenseforge.web.request.application.UpdateApplicationRequest;

import java.util.List;
import java.util.UUID;

public interface ApplicationService {
    ApplicationDTO createApplication(CreateApplicationRequest request);

    ApplicationDTO getApplicationById(UUID id);

    List<ApplicationDTO> getApplicationsByRealmId(UUID realmId, int page, int size);

    ApplicationDTO updateApplication(UUID id, UpdateApplicationRequest request);

    void deleteApplication(UUID id);

    long countByRealmId(UUID realmId);
}
