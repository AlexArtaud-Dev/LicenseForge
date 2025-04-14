package com.alexartauddev.licenseforge.application.team.service.impl;

import com.alexartauddev.licenseforge.application.team.mapper.TeamPermissionMapper;
import com.alexartauddev.licenseforge.application.team.service.TeamPermissionService;
import com.alexartauddev.licenseforge.domain.application.entity.Application;
import com.alexartauddev.licenseforge.domain.application.repository.ApplicationRepository;
import com.alexartauddev.licenseforge.domain.team.entity.Team;
import com.alexartauddev.licenseforge.domain.team.entity.TeamPermission;
import com.alexartauddev.licenseforge.domain.team.repository.TeamPermissionRepository;
import com.alexartauddev.licenseforge.domain.team.repository.TeamRepository;
import com.alexartauddev.licenseforge.web.dto.team.TeamPermissionDTO;
import com.alexartauddev.licenseforge.web.exception.application.ApplicationNotFoundException;
import com.alexartauddev.licenseforge.web.exception.team.DuplicateTeamPermissionException;
import com.alexartauddev.licenseforge.web.exception.team.TeamNotFoundException;
import com.alexartauddev.licenseforge.web.exception.team.TeamPermissionNotFoundException;
import com.alexartauddev.licenseforge.web.request.team.CreateTeamPermissionRequest;
import com.alexartauddev.licenseforge.web.request.team.UpdateTeamPermissionRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TeamPermissionServiceImpl implements TeamPermissionService {

    private final TeamPermissionRepository teamPermissionRepository;
    private final TeamRepository teamRepository;
    private final ApplicationRepository applicationRepository;
    private final TeamPermissionMapper teamPermissionMapper;

    @Override
    @Transactional
    public TeamPermissionDTO createTeamPermission(CreateTeamPermissionRequest request) {
        // Verify team exists
        Team team = teamRepository.findById(request.getTeamId())
                .orElseThrow(() -> TeamNotFoundException.withId(request.getTeamId()));

        // Verify application exists
        Application application = applicationRepository.findById(request.getAppId())
                .orElseThrow(() -> ApplicationNotFoundException.withId(request.getAppId()));

        // Check if permission already exists
        if (teamPermissionRepository.findByTeamIdAndAppId(request.getTeamId(), request.getAppId()).isPresent()) {
            throw new DuplicateTeamPermissionException(request.getTeamId(), request.getAppId());
        }

        TeamPermission teamPermission = TeamPermission.builder()
                .teamId(request.getTeamId())
                .appId(request.getAppId())
                .permissionType(request.getPermissionType())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        TeamPermission savedTeamPermission = teamPermissionRepository.save(teamPermission);

        TeamPermissionDTO dto = teamPermissionMapper.toDTO(savedTeamPermission);
        dto.setTeamName(team.getName());
        dto.setAppName(application.getName());

        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public TeamPermissionDTO getTeamPermissionById(UUID id) {
        TeamPermission teamPermission = teamPermissionRepository.findById(id)
                .orElseThrow(() -> TeamPermissionNotFoundException.withId(id));

        TeamPermissionDTO dto = teamPermissionMapper.toDTO(teamPermission);

        // Get team name
        teamRepository.findById(teamPermission.getTeamId())
                .ifPresent(team -> dto.setTeamName(team.getName()));

        // Get application name
        applicationRepository.findById(teamPermission.getAppId())
                .ifPresent(app -> dto.setAppName(app.getName()));

        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public TeamPermissionDTO getTeamPermissionByTeamIdAndAppId(UUID teamId, UUID appId) {
        TeamPermission teamPermission = teamPermissionRepository.findByTeamIdAndAppId(teamId, appId)
                .orElseThrow(() -> new TeamPermissionNotFoundException(
                        "Team permission not found for team " + teamId + " and application " + appId));

        TeamPermissionDTO dto = teamPermissionMapper.toDTO(teamPermission);

        // Get team name
        teamRepository.findById(teamPermission.getTeamId())
                .ifPresent(team -> dto.setTeamName(team.getName()));

        // Get application name
        applicationRepository.findById(teamPermission.getAppId())
                .ifPresent(app -> dto.setAppName(app.getName()));

        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<TeamPermissionDTO> getTeamPermissionsByTeamId(UUID teamId) {
        return teamPermissionRepository.findByTeamId(teamId).stream()
                .map(permission -> {
                    TeamPermissionDTO dto = teamPermissionMapper.toDTO(permission);

                    // Get team name
                    teamRepository.findById(permission.getTeamId())
                            .ifPresent(team -> dto.setTeamName(team.getName()));

                    // Get application name
                    applicationRepository.findById(permission.getAppId())
                            .ifPresent(app -> dto.setAppName(app.getName()));

                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TeamPermissionDTO> getTeamPermissionsByAppId(UUID appId) {
        return teamPermissionRepository.findByAppId(appId).stream()
                .map(permission -> {
                    TeamPermissionDTO dto = teamPermissionMapper.toDTO(permission);

                    // Get team name
                    teamRepository.findById(permission.getTeamId())
                            .ifPresent(team -> dto.setTeamName(team.getName()));

                    // Get application name
                    applicationRepository.findById(permission.getAppId())
                            .ifPresent(app -> dto.setAppName(app.getName()));

                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TeamPermissionDTO> getTeamPermissionsByTeamIdAndPermissionType(UUID teamId, TeamPermission.PermissionType permissionType) {
        return teamPermissionRepository.findByTeamIdAndPermissionType(teamId, permissionType).stream()
                .map(permission -> {
                    TeamPermissionDTO dto = teamPermissionMapper.toDTO(permission);

                    // Get team name
                    teamRepository.findById(permission.getTeamId())
                            .ifPresent(team -> dto.setTeamName(team.getName()));

                    // Get application name
                    applicationRepository.findById(permission.getAppId())
                            .ifPresent(app -> dto.setAppName(app.getName()));

                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public TeamPermissionDTO updateTeamPermission(UUID id, UpdateTeamPermissionRequest request) {
        TeamPermission teamPermission = teamPermissionRepository.findById(id)
                .orElseThrow(() -> TeamPermissionNotFoundException.withId(id));

        teamPermission.setPermissionType(request.getPermissionType());
        teamPermission.setUpdatedAt(LocalDateTime.now());

        TeamPermission updatedTeamPermission = teamPermissionRepository.save(teamPermission);

        TeamPermissionDTO dto = teamPermissionMapper.toDTO(updatedTeamPermission);

        // Get team name
        teamRepository.findById(updatedTeamPermission.getTeamId())
                .ifPresent(team -> dto.setTeamName(team.getName()));

        // Get application name
        applicationRepository.findById(updatedTeamPermission.getAppId())
                .ifPresent(app -> dto.setAppName(app.getName()));

        return dto;
    }

    @Override
    @Transactional
    public void deleteTeamPermission(UUID id) {
        TeamPermission teamPermission = teamPermissionRepository.findById(id)
                .orElseThrow(() -> TeamPermissionNotFoundException.withId(id));

        teamPermissionRepository.delete(teamPermission);
    }

    @Override
    @Transactional
    public void deleteTeamPermissionsByTeamId(UUID teamId) {
        teamPermissionRepository.deleteByTeamId(teamId);
    }

    @Override
    @Transactional
    public void deleteTeamPermissionsByAppId(UUID appId) {
        teamPermissionRepository.deleteByAppId(appId);
    }
}