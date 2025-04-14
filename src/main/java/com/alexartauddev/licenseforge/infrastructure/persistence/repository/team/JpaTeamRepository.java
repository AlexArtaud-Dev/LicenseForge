package com.alexartauddev.licenseforge.infrastructure.persistence.repository.team;

import com.alexartauddev.licenseforge.domain.team.entity.Team;
import com.alexartauddev.licenseforge.domain.team.repository.TeamRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaTeamRepository implements TeamRepository {

    private final SpringDataTeamRepository repository;

    public JpaTeamRepository(SpringDataTeamRepository repository) {
        this.repository = repository;
    }

    @Override
    public Team save(Team team) {
        return repository.save(team);
    }

    @Override
    public Optional<Team> findById(UUID id) {
        return repository.findById(id);
    }

    @Override
    public List<Team> findAll() {
        return repository.findAll();
    }

    @Override
    public List<Team> findByCompanyId(UUID companyId) {
        return repository.findByCompanyId(companyId);
    }

    @Override
    public Optional<Team> findByNameAndCompanyId(String name, UUID companyId) {
        return repository.findByNameAndCompanyId(name, companyId);
    }

    @Override
    public long countByCompanyId(UUID companyId) {
        return repository.countByCompanyId(companyId);
    }

    @Override
    public void delete(Team team) {
        repository.delete(team);
    }
}