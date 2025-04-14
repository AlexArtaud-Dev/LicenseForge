package com.alexartauddev.licenseforge.application.team.service.impl;

import com.alexartauddev.licenseforge.application.team.mapper.TeamMapper;
import com.alexartauddev.licenseforge.application.team.service.TeamService;
import com.alexartauddev.licenseforge.domain.company.entity.Company;
import com.alexartauddev.licenseforge.domain.company.repository.CompanyRepository;
import com.alexartauddev.licenseforge.domain.team.entity.Team;
import com.alexartauddev.licenseforge.domain.team.repository.TeamRepository;
import com.alexartauddev.licenseforge.domain.user.repository.UserRepository;
import com.alexartauddev.licenseforge.web.dto.team.TeamDTO;
import com.alexartauddev.licenseforge.web.exception.company.CompanyNotFoundException;
import com.alexartauddev.licenseforge.web.exception.team.TeamNotFoundException;
import com.alexartauddev.licenseforge.web.request.team.CreateTeamRequest;
import com.alexartauddev.licenseforge.web.request.team.UpdateTeamRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TeamServiceImpl implements TeamService {

    private final TeamRepository teamRepository;
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;
    private final TeamMapper teamMapper;

    @Override
    @Transactional
    public TeamDTO createTeam(CreateTeamRequest request) {
        // Verify company exists
        Company company = companyRepository.findById(request.getCompanyId())
                .orElseThrow(() -> CompanyNotFoundException.withId(request.getCompanyId()));

        Team team = Team.builder()
                .name(request.getName())
                .companyId(request.getCompanyId())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Team savedTeam = teamRepository.save(team);

        TeamDTO dto = teamMapper.toDTO(savedTeam);
        dto.setCompanyName(company.getName());
        dto.setMembersCount(0); // New team has no members

        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public TeamDTO getTeamById(UUID id) {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> TeamNotFoundException.withId(id));

        TeamDTO dto = teamMapper.toDTO(team);

        // Get company name
        companyRepository.findById(team.getCompanyId()).ifPresent(company ->
                dto.setCompanyName(company.getName()));

        // Get members count
        dto.setMembersCount(userRepository.countByTeamId(team.getId()));

        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<TeamDTO> getTeamsByCompanyId(UUID companyId, int page, int size) {
        return teamRepository.findByCompanyId(companyId).stream()
                .skip((long) page * size)
                .limit(size)
                .map(team -> {
                    TeamDTO dto = teamMapper.toDTO(team);

                    // Get company name
                    companyRepository.findById(team.getCompanyId()).ifPresent(company ->
                            dto.setCompanyName(company.getName()));

                    // Get members count
                    dto.setMembersCount(userRepository.countByTeamId(team.getId()));

                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public TeamDTO updateTeam(UUID id, UpdateTeamRequest request) {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> TeamNotFoundException.withId(id));

        if (request.getName() != null) {
            team.setName(request.getName());
        }

        team.setUpdatedAt(LocalDateTime.now());

        Team updatedTeam = teamRepository.save(team);

        TeamDTO dto = teamMapper.toDTO(updatedTeam);

        // Get company name
        companyRepository.findById(updatedTeam.getCompanyId()).ifPresent(company ->
                dto.setCompanyName(company.getName()));

        // Get members count
        dto.setMembersCount(userRepository.countByTeamId(updatedTeam.getId()));

        return dto;
    }

    @Override
    @Transactional
    public void deleteTeam(UUID id) {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> TeamNotFoundException.withId(id));

        teamRepository.delete(team);
    }

    @Override
    @Transactional(readOnly = true)
    public long countByCompanyId(UUID companyId) {
        return teamRepository.countByCompanyId(companyId);
    }
}