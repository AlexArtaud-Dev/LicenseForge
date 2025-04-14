package com.alexartauddev.licenseforge.infrastructure.persistence.repository.license;

import com.alexartauddev.licenseforge.domain.license.entity.Activation;
import com.alexartauddev.licenseforge.domain.license.repository.ActivationRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * JPA implementation of the ActivationRepository interface
 */
@Repository
public class JpaActivationRepository implements ActivationRepository {

    private final SpringDataActivationRepository repository;

    public JpaActivationRepository(SpringDataActivationRepository repository) {
        this.repository = repository;
    }

    @Override
    public Activation save(Activation activation) {
        return repository.save(activation);
    }

    @Override
    public Optional<Activation> findById(UUID id) {
        return repository.findById(id);
    }

    @Override
    public List<Activation> findAll() {
        return repository.findAll();
    }

    @Override
    public List<Activation> findByLicenseId(UUID licenseId) {
        return repository.findByLicenseId(licenseId);
    }

    @Override
    public Optional<Activation> findByLicenseIdAndHardwareId(UUID licenseId, String hardwareId) {
        return repository.findByLicenseIdAndHardwareId(licenseId, hardwareId);
    }

    @Override
    public long countByLicenseId(UUID licenseId) {
        return repository.countByLicenseId(licenseId);
    }

    @Override
    public List<Activation> findInactiveActivations(LocalDateTime threshold) {
        return repository.findInactiveActivations(threshold);
    }

    @Override
    public long deleteByLicenseId(UUID licenseId) {
        return repository.deleteByLicenseId(licenseId);
    }

    @Override
    public void delete(Activation activation) {
        repository.delete(activation);
    }
}