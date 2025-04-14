package com.alexartauddev.licenseforge.infrastructure.persistence.repository.application;

import com.alexartauddev.licenseforge.domain.application.entity.Application;
import com.alexartauddev.licenseforge.domain.application.repository.ApplicationRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * JPA implementation of the ApplicationRepository interface
 */
@Repository
public class JpaApplicationRepository implements ApplicationRepository {

    private final SpringDataApplicationRepository repository;

    public JpaApplicationRepository(SpringDataApplicationRepository repository) {
        this.repository = repository;
    }

    @Override
    public Application save(Application application) {
        return repository.save(application);
    }

    @Override
    public Optional<Application> findById(UUID id) {
        return repository.findById(id);
    }

    @Override
    public List<Application> findAll() {
        return repository.findAll();
    }

    @Override
    public List<Application> findByRealmId(UUID realmId) {
        return repository.findByRealmId(realmId);
    }

    @Override
    public Optional<Application> findByNameAndRealmId(String name, UUID realmId) {
        return repository.findByNameAndRealmId(name, realmId);
    }

    @Override
    public long countByRealmId(UUID realmId) {
        return repository.countByRealmId(realmId);
    }

    @Override
    public void delete(Application application) {
        repository.delete(application);
    }
}
