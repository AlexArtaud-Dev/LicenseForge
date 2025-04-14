package com.alexartauddev.licenseforge.application.user.service;

import java.util.List;
import java.util.UUID;

public interface UserTeamService {
    /**
     * Add a user to a team
     *
     * @param userId The user ID
     * @param teamId The team ID
     */
    void addUserToTeam(UUID userId, UUID teamId);

    /**
     * Remove a user from a team
     *
     * @param userId The user ID
     * @param teamId The team ID
     */
    void removeUserFromTeam(UUID userId, UUID teamId);

    /**
     * Get all teams for a user
     *
     * @param userId The user ID
     * @return List of team IDs
     */
    List<UUID> getTeamsForUser(UUID userId);

    /**
     * Get all users in a team
     *
     * @param teamId The team ID
     * @return List of user IDs
     */
    List<UUID> getUsersInTeam(UUID teamId);
}