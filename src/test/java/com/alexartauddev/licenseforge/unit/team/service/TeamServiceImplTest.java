package com.alexartauddev.licenseforge.unit.team.service;

import com.alexartauddev.licenseforge.application.team.mapper.TeamMapper;
import com.alexartauddev.licenseforge.application.team.service.impl.TeamServiceImpl;
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
class TeamServiceImplTest {

    @Mock
    private TeamRepository teamRepository;

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TeamMapper teamMapper;

    @InjectMocks
    private TeamServiceImpl teamService;

    private UUID teamId;
    private UUID companyId;
    private Team team;
    private TeamDTO teamDTO;
    private Company company;
    private CreateTeamRequest createTeamRequest;

    @BeforeEach
    void setUp() {
        teamId = UUID.randomUUID();
        companyId = UUID.randomUUID();

        team = Team.builder()
                .id(teamId)
                .name("Test Team")
                .companyId(companyId)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        company = Company.builder()
                .id(companyId)
                .name("Test Company")
                .build();

        teamDTO = TeamDTO.builder()
                .id(teamId)
                .name("Test Team")
                .companyId(companyId)
                .companyName("Test Company")
                .membersCount(3)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        createTeamRequest = CreateTeamRequest.builder()
                .name("Test Team")
                .companyId(companyId)
                .build();
    }

    @Test
    void createTeam_ValidRequest_ShouldCreateTeam() {
        // Arrange
        when(companyRepository.findById(companyId)).thenReturn(Optional.of(company));
        when(teamRepository.save(any(Team.class))).thenReturn(team);
        when(teamMapper.toDTO(any(Team.class))).thenReturn(teamDTO);

        // Act
        TeamDTO result = teamService.createTeam(createTeamRequest);

        // Assert
        assertNotNull(result);
        assertEquals(teamDTO, result);
        verify(companyRepository).findById(companyId);
        verify(teamRepository).save(any(Team.class));
        verify(teamMapper).toDTO(any(Team.class));
    }

    @Test
    void createTeam_NonExistingCompany_ShouldThrowException() {
        // Arrange
        when(companyRepository.findById(companyId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(CompanyNotFoundException.class, () -> teamService.createTeam(createTeamRequest));
        verify(companyRepository).findById(companyId);
        verify(teamRepository, never()).save(any(Team.class));
    }

    @Test
    void getTeamById_ExistingTeam_ShouldReturnTeam() {
        // Arrange
        when(teamRepository.findById(teamId)).thenReturn(Optional.of(team));
        when(teamMapper.toDTO(team)).thenReturn(teamDTO);
        when(companyRepository.findById(companyId)).thenReturn(Optional.of(company));
        when(userRepository.countByTeamId(teamId)).thenReturn(3L);

        // Act
        TeamDTO result = teamService.getTeamById(teamId);

        // Assert
        assertNotNull(result);
        assertEquals(teamDTO, result);
        verify(teamRepository).findById(teamId);
        verify(teamMapper).toDTO(team);
        verify(companyRepository).findById(companyId);
        verify(userRepository).countByTeamId(teamId);
    }

    @Test
    void getTeamById_NonExistingTeam_ShouldThrowException() {
        // Arrange
        when(teamRepository.findById(teamId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(TeamNotFoundException.class, () -> teamService.getTeamById(teamId));
        verify(teamRepository).findById(teamId);
        verify(teamMapper, never()).toDTO(any(Team.class));
    }

    @Test
    void getTeamsByCompanyId_ShouldReturnTeams() {
        // Arrange
        List<Team> teams = Arrays.asList(team, team);

        when(teamRepository.findByCompanyId(companyId)).thenReturn(teams);
        when(teamMapper.toDTO(any(Team.class))).thenReturn(teamDTO);
        when(companyRepository.findById(companyId)).thenReturn(Optional.of(company));
        when(userRepository.countByTeamId(any(UUID.class))).thenReturn(3L);

        // Act
        List<TeamDTO> result = teamService.getTeamsByCompanyId(companyId, 0, 10);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(teamDTO, result.get(0));
        verify(teamRepository).findByCompanyId(companyId);
        verify(teamMapper, times(2)).toDTO(any(Team.class));
        verify(companyRepository, times(2)).findById(companyId);
        verify(userRepository, times(2)).countByTeamId(any(UUID.class));
    }

    @Test
    void updateTeam_ExistingTeam_ShouldUpdateTeam() {
        // Arrange
        UpdateTeamRequest request = UpdateTeamRequest.builder()
                .name("Updated Team")
                .build();

        when(teamRepository.findById(teamId)).thenReturn(Optional.of(team));
        when(teamRepository.save(any(Team.class))).thenReturn(team);
        when(teamMapper.toDTO(team)).thenReturn(teamDTO);
        when(companyRepository.findById(companyId)).thenReturn(Optional.of(company));
        when(userRepository.countByTeamId(teamId)).thenReturn(3L);

        // Act
        TeamDTO result = teamService.updateTeam(teamId, request);

        // Assert
        assertNotNull(result);
        assertEquals(teamDTO, result);
        verify(teamRepository).findById(teamId);
        verify(teamRepository).save(team);
        verify(teamMapper).toDTO(team);
        verify(companyRepository).findById(companyId);
        verify(userRepository).countByTeamId(teamId);

        // Verify that the team was updated with the new name
        assertEquals("Updated Team", team.getName());
    }

    @Test
    void updateTeam_NonExistingTeam_ShouldThrowException() {
        // Arrange
        UpdateTeamRequest request = new UpdateTeamRequest();
        when(teamRepository.findById(teamId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(TeamNotFoundException.class, () -> teamService.updateTeam(teamId, request));
        verify(teamRepository).findById(teamId);
        verify(teamRepository, never()).save(any(Team.class));
    }

    @Test
    void deleteTeam_ExistingTeam_ShouldDeleteTeam() {
        // Arrange
        when(teamRepository.findById(teamId)).thenReturn(Optional.of(team));
        doNothing().when(teamRepository).delete(team);

        // Act
        teamService.deleteTeam(teamId);

        // Assert
        verify(teamRepository).findById(teamId);
        verify(teamRepository).delete(team);
    }

    @Test
    void deleteTeam_NonExistingTeam_ShouldThrowException() {
        // Arrange
        when(teamRepository.findById(teamId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(TeamNotFoundException.class, () -> teamService.deleteTeam(teamId));
        verify(teamRepository).findById(teamId);
        verify(teamRepository, never()).delete(any(Team.class));
    }

    @Test
    void countByCompanyId_ShouldReturnCount() {
        // Arrange
        when(teamRepository.countByCompanyId(companyId)).thenReturn(5L);

        // Act
        long result = teamService.countByCompanyId(companyId);

        // Assert
        assertEquals(5L, result);
        verify(teamRepository).countByCompanyId(companyId);
    }
}
