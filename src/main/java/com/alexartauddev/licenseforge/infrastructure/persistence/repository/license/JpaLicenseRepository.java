package com.alexartauddev.licenseforge.infrastructure.persistence.repository.license;

import com.alexartauddev.licenseforge.domain.license.entity.License;
import com.alexartauddev.licenseforge.domain.license.repository.LicenseRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * JPA implementation of the LicenseRepository interface
 */
@Repository
public class JpaLicenseRepository implements LicenseRepository {

    private final SpringDataLicenseRepository repository;

    public JpaLicenseRepository(SpringDataLicenseRepository repository) {
        this.repository = repository;
    }

    @Override
    public License save(License license) {
        return repository.save(license);
    }

    @Override
    public Optional<License> findById(UUID id) {
        return repository.findById(id);
    }

    @Override
    public List<License> findAll() {
        return repository.findAll();
    }

    @Override
    public Optional<License> findByLicenseKey(String licenseKey) {
        return repository.findByLicenseKey(licenseKey);
    }

    @Override
    public List<License> findByAppId(UUID appId) {
        return repository.findByAppId(appId);
    }

    @Override
    public List<License> findByCustomerId(String customerId) {
        return repository.findByCustomerId(customerId);
    }

    @Override
    public List<License> findActiveByAppId(UUID appId, LocalDateTime now) {
        return repository.findActiveByAppId(appId, now);
    }

    @Override
    public List<License> findExpiringLicenses(UUID appId, LocalDateTime start, LocalDateTime end) {
        return repository.findExpiringLicenses(appId, start, end);
    }

    @Override
    public long countActiveByAppId(UUID appId, LocalDateTime now) {
        return repository.countActiveByAppId(appId, now);
    }

    @Override
    public void delete(License license) {
        repository.delete(license);
    }
}