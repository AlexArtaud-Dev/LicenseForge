package com.alexartauddev.licenseforge.infrastructure.persistence.repository.team;

import com.alexartauddev.licenseforge.domain.team.entity.TeamPermission;
import com.alexartauddev.licenseforge.domain.team.repository.TeamPermissionRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * JPA implementation of the TeamPermissionRepository interface
 */
@Repository
public class JpaTeamPermissionRepository implements TeamPermissionRepository {

    private final SpringDataTeamPermissionRepository repository;

    public JpaTeamPermissionRepository(SpringDataTeamPermissionRepository repository) {
        this.repository = repository;
    }

    @Override
    public TeamPermission save(TeamPermission teamPermission) {
        return repository.save(teamPermission);
    }

    @Override
    public Optional<TeamPermission> findById(UUID id) {
        return repository.findById(id);
    }

    @Override
    public List<TeamPermission> findAll() {
        return repository.findAll();
    }

    @Override
    public List<TeamPermission> findByTeamId(UUID teamId) {
        return repository.findByTeamId(teamId);
    }

    @Override
    public List<TeamPermission> findByAppId(UUID appId) {
        return repository.findByAppId(appId);
    }

    @Override
    public Optional<TeamPermission> findByTeamIdAndAppId(UUID teamId, UUID appId) {
        return repository.findByTeamIdAndAppId(teamId, appId);
    }

    @Override
    public List<TeamPermission> findByTeamIdAndPermissionType(UUID teamId, TeamPermission.PermissionType permissionType) {
        return repository.findByTeamIdAndPermissionType(teamId, permissionType);
    }

    @Override
    public long deleteByTeamId(UUID teamId) {
        return repository.deleteByTeamId(teamId);
    }

    @Override
    public long deleteByAppId(UUID appId) {
        return repository.deleteByAppId(appId);
    }

    @Override
    public void delete(TeamPermission teamPermission) {
        repository.delete(teamPermission);
    }
}
