package com.alexartauddev.licenseforge.domain.team.repository;

import com.alexartauddev.licenseforge.domain.team.entity.TeamPermission;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for TeamPermission entities
 * This is a technology-agnostic interface in the domain layer
 */
public interface TeamPermissionRepository {

    /**
     * Save a team permission
     */
    TeamPermission save(TeamPermission teamPermission);

    /**
     * Find a team permission by its ID
     */
    Optional<TeamPermission> findById(UUID id);

    /**
     * Find all team permissions
     */
    List<TeamPermission> findAll();

    /**
     * Find all permissions for a specific team
     */
    List<TeamPermission> findByTeamId(UUID teamId);

    /**
     * Find all permissions for a specific application
     */
    List<TeamPermission> findByAppId(UUID appId);

    /**
     * Find the permission for a team on a specific application
     */
    Optional<TeamPermission> findByTeamIdAndAppId(UUID teamId, UUID appId);

    /**
     * Find all permissions of a specific type for a team
     */
    List<TeamPermission> findByTeamIdAndPermissionType(UUID teamId, TeamPermission.PermissionType permissionType);

    /**
     * Delete all permissions for a specific team
     */
    long deleteByTeamId(UUID teamId);

    /**
     * Delete all permissions for a specific application
     */
    long deleteByAppId(UUID appId);

    /**
     * Delete a team permission
     */
    void delete(TeamPermission teamPermission);
}