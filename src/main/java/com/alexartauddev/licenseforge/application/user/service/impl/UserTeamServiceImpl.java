package com.alexartauddev.licenseforge.application.user.service.impl;

import com.alexartauddev.licenseforge.application.user.service.UserTeamService;
import com.alexartauddev.licenseforge.domain.team.entity.Team;
import com.alexartauddev.licenseforge.domain.team.repository.TeamRepository;
import com.alexartauddev.licenseforge.domain.user.entity.User;
import com.alexartauddev.licenseforge.domain.user.entity.UserTeam;
import com.alexartauddev.licenseforge.domain.user.repository.UserRepository;
import com.alexartauddev.licenseforge.domain.user.repository.UserTeamRepository;
import com.alexartauddev.licenseforge.web.exception.team.TeamNotFoundException;
import com.alexartauddev.licenseforge.web.exception.user.UserNotFoundException;
import com.alexartauddev.licenseforge.web.exception.user.UserTeamException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserTeamServiceImpl implements UserTeamService {

    private final UserTeamRepository userTeamRepository;
    private final UserRepository userRepository;
    private final TeamRepository teamRepository;

    @Override
    @Transactional
    public void addUserToTeam(UUID userId, UUID teamId) {
        // Verify user exists
        User user = userRepository.findById(userId)
                .orElseThrow(() -> UserNotFoundException.withId(userId));

        // Verify team exists
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> TeamNotFoundException.withId(teamId));

        // Verify user and team are from the same company
        if (!user.getCompanyId().equals(team.getCompanyId())) {
            throw new UserTeamException("User and team must belong to the same company");
        }

        // Check if relationship already exists
        if (userTeamRepository.findByUserIdAndTeamId(userId, teamId).isPresent()) {
            // Already exists, no need to add again
            return;
        }

        // Create new relationship
        UserTeam userTeam = UserTeam.builder()
                .userId(userId)
                .teamId(teamId)
                .createdAt(LocalDateTime.now())
                .build();

        userTeamRepository.save(userTeam);
        log.info("User {} added to team {}", userId, teamId);
    }

    @Override
    @Transactional
    public void removeUserFromTeam(UUID userId, UUID teamId) {
        // Verify the relationship exists
        userTeamRepository.findByUserIdAndTeamId(userId, teamId)
                .orElseThrow(() -> new UserTeamException("User is not a member of this team"));

        // Delete the relationship
        userTeamRepository.deleteByUserIdAndTeamId(userId, teamId);
        log.info("User {} removed from team {}", userId, teamId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UUID> getTeamsForUser(UUID userId) {
        // Verify user exists
        if (!userTeamRepository.existsByUserId(userId)) {
            throw UserNotFoundException.withId(userId);
        }

        return userTeamRepository.findByUserId(userId).stream()
                .map(UserTeam::getTeamId)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UUID> getUsersInTeam(UUID teamId) {
        // Verify team exists
        if (!userTeamRepository.existsByTeamId(teamId)) {
            throw TeamNotFoundException.withId(teamId);
        }

        return userTeamRepository.findByTeamId(teamId).stream()
                .map(UserTeam::getUserId)
                .collect(Collectors.toList());
    }
}