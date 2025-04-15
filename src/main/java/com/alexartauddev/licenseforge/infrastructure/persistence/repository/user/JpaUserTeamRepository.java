package com.alexartauddev.licenseforge.infrastructure.persistence.repository.user;

import com.alexartauddev.licenseforge.domain.user.entity.UserTeam;
import com.alexartauddev.licenseforge.domain.user.repository.UserTeamRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * JPA implementation of the UserTeamRepository interface
 */
@Repository
public class JpaUserTeamRepository implements UserTeamRepository {

    private final SpringDataUserTeamRepository repository;

    public JpaUserTeamRepository(SpringDataUserTeamRepository repository) {
        this.repository = repository;
    }

    @Override
    public UserTeam save(UserTeam userTeam) {
        return repository.save(userTeam);
    }

    @Override
    public Optional<UserTeam> findById(UUID id) {
        return repository.findById(id);
    }

    @Override
    public List<UserTeam> findAll() {
        return repository.findAll();
    }

    @Override
    public List<UserTeam> findByUserId(UUID userId) {
        return repository.findByUserId(userId);
    }

    @Override
    public List<UserTeam> findByTeamId(UUID teamId) {
        return repository.findByTeamId(teamId);
    }

    @Override
    public Optional<UserTeam> findByUserIdAndTeamId(UUID userId, UUID teamId) {
        return repository.findByUserIdAndTeamId(userId, teamId);
    }

    @Override
    public void delete(UserTeam userTeam) {
        repository.delete(userTeam);
    }

    @Override
    public void deleteByUserIdAndTeamId(UUID userId, UUID teamId) {
        repository.deleteByUserIdAndTeamId(userId, teamId);
    }

    @Override
    public boolean existsByUserId(UUID userId) {
        return repository.existsByUserId(userId);
    }

    @Override
    public boolean existsByTeamId(UUID teamId) {
        return repository.existsByTeamId(teamId);
    }
}