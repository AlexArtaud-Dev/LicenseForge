package com.alexartauddev.licenseforge.infrastructure.persistence.repository.realm;

import com.alexartauddev.licenseforge.domain.realm.entity.Realm;
import com.alexartauddev.licenseforge.domain.realm.repository.RealmRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * JPA implementation of the RealmRepository interface
 */
@Repository
public class JpaRealmRepository implements RealmRepository {

    private final SpringDataRealmRepository repository;

    public JpaRealmRepository(SpringDataRealmRepository repository) {
        this.repository = repository;
    }

    @Override
    public Realm save(Realm realm) {
        return repository.save(realm);
    }

    @Override
    public Optional<Realm> findById(UUID id) {
        return repository.findById(id);
    }

    @Override
    public List<Realm> findAll() {
        return repository.findAll();
    }

    @Override
    public List<Realm> findByCompanyId(UUID companyId) {
        return repository.findByCompanyId(companyId);
    }

    @Override
    public Optional<Realm> findByNameAndCompanyId(String name, UUID companyId) {
        return repository.findByNameAndCompanyId(name, companyId);
    }

    @Override
    public long countByCompanyId(UUID companyId) {
        return repository.countByCompanyId(companyId);
    }

    @Override
    public void delete(Realm realm) {
        repository.delete(realm);
    }
}