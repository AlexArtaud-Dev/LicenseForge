package com.alexartauddev.licenseforge.application.team.service;

import com.alexartauddev.licenseforge.domain.team.entity.TeamPermission;
import com.alexartauddev.licenseforge.web.dto.team.TeamPermissionDTO;
import com.alexartauddev.licenseforge.web.request.team.CreateTeamPermissionRequest;
import com.alexartauddev.licenseforge.web.request.team.UpdateTeamPermissionRequest;

import java.util.List;
import java.util.UUID;

public interface TeamPermissionService {
    TeamPermissionDTO createTeamPermission(CreateTeamPermissionRequest request);

    TeamPermissionDTO getTeamPermissionById(UUID id);

    TeamPermissionDTO getTeamPermissionByTeamIdAndAppId(UUID teamId, UUID appId);

    List<TeamPermissionDTO> getTeamPermissionsByTeamId(UUID teamId);

    List<TeamPermissionDTO> getTeamPermissionsByAppId(UUID appId);

    List<TeamPermissionDTO> getTeamPermissionsByTeamIdAndPermissionType(UUID teamId, TeamPermission.PermissionType permissionType);

    TeamPermissionDTO updateTeamPermission(UUID id, UpdateTeamPermissionRequest request);

    void deleteTeamPermission(UUID id);

    void deleteTeamPermissionsByTeamId(UUID teamId);

    void deleteTeamPermissionsByAppId(UUID appId);
}