package com.alexartauddev.licenseforge.domain.user.repository;

import com.alexartauddev.licenseforge.domain.user.entity.UserTeam;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for UserTeam entities
 * This is a technology-agnostic interface in the domain layer
 */
public interface UserTeamRepository {

    /**
     * Save a user team relationship
     */
    UserTeam save(UserTeam userTeam);

    /**
     * Find a user team relationship by ID
     */
    Optional<UserTeam> findById(UUID id);

    /**
     * Find all user team relationships
     */
    List<UserTeam> findAll();

    /**
     * Find all team relationships for a user
     */
    List<UserTeam> findByUserId(UUID userId);

    /**
     * Find all user relationships for a team
     */
    List<UserTeam> findByTeamId(UUID teamId);

    /**
     * Find a specific user-team relationship
     */
    Optional<UserTeam> findByUserIdAndTeamId(UUID userId, UUID teamId);

    /**
     * Delete a user-team relationship
     */
    void delete(UserTeam userTeam);

    /**
     * Delete a user-team relationship by user ID and team ID
     */
    void deleteByUserIdAndTeamId(UUID userId, UUID teamId);

    /**
     * Check if a user exists by ID
     */
    boolean existsByUserId(UUID userId);

    /**
     * Check if a team exists by ID
     */
    boolean existsByTeamId(UUID teamId);
}