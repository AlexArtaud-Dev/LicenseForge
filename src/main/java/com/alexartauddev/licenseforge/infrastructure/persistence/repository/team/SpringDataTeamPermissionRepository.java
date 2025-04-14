package com.alexartauddev.licenseforge.infrastructure.persistence.repository.team;

import com.alexartauddev.licenseforge.domain.team.entity.TeamPermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data JPA repository for TeamPermission entities
 */
@Repository
interface SpringDataTeamPermissionRepository extends JpaRepository<TeamPermission, UUID> {

    List<TeamPermission> findByTeamId(UUID teamId);

    List<TeamPermission> findByAppId(UUID appId);

    Optional<TeamPermission> findByTeamIdAndAppId(UUID teamId, UUID appId);

    List<TeamPermission> findByTeamIdAndPermissionType(UUID teamId, TeamPermission.PermissionType permissionType);

    long deleteByTeamId(UUID teamId);

    long deleteByAppId(UUID appId);
}