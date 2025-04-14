package com.alexartauddev.licenseforge.infrastructure.persistence.repository.user;

import com.alexartauddev.licenseforge.domain.user.entity.User;
import com.alexartauddev.licenseforge.domain.user.repository.UserRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * JPA implementation of the UserRepository interface
 */
@Repository
public class JpaUserRepository implements UserRepository {

    private final SpringDataUserRepository repository;

    public JpaUserRepository(SpringDataUserRepository repository) {
        this.repository = repository;
    }

    @Override
    public User save(User user) {
        return repository.save(user);
    }

    @Override
    public Optional<User> findById(UUID id) {
        return repository.findById(id);
    }

    @Override
    public List<User> findAll() {
        return repository.findAll();
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return repository.findByEmail(email);
    }

    @Override
    public List<User> findByCompanyId(UUID companyId) {
        return repository.findByCompanyId(companyId);
    }

    @Override
    public List<User> findByTeamId(UUID teamId) {
        return repository.findByTeamId(teamId);
    }

    @Override
    public List<User> findByRoleAndCompanyId(User.Role role, UUID companyId) {
        return repository.findByRoleAndCompanyId(role, companyId);
    }

    @Override
    public boolean existsByEmail(String email) {
        return repository.existsByEmail(email);
    }

    @Override
    public long countByCompanyId(UUID companyId) {
        return repository.countByCompanyId(companyId);
    }

    @Override
    public long countByTeamId(UUID teamId) {
        return repository.countByTeamId(teamId);
    }

    @Override
    public void delete(User user) {
        repository.delete(user);
    }
}