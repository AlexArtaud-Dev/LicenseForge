package com.alexartauddev.licenseforge.application.team.service;


import com.alexartauddev.licenseforge.web.dto.team.TeamDTO;
import com.alexartauddev.licenseforge.web.request.team.CreateTeamRequest;
import com.alexartauddev.licenseforge.web.request.team.UpdateTeamRequest;

import java.util.List;
import java.util.UUID;

public interface TeamService {
    TeamDTO createTeam(CreateTeamRequest request);

    TeamDTO getTeamById(UUID id);

    List<TeamDTO> getTeamsByCompanyId(UUID companyId, int page, int size);

    TeamDTO updateTeam(UUID id, UpdateTeamRequest request);

    void deleteTeam(UUID id);

    long countByCompanyId(UUID companyId);
}