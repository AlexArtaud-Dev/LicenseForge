package com.alexartauddev.licenseforge.domain.license.repository;

import com.alexartauddev.licenseforge.domain.license.entity.License;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface LicenseRepository extends JpaRepository<License, UUID> {

    Optional<License> findByLicenseKey(String licenseKey);

    List<License> findByCustomerId(String customerId);

    List<License> findByProductId(String productId);

    List<License> findByExpiresAtBefore(LocalDateTime date);

    List<License> findByRevokedTrue();

    List<License> findByRevokedFalse();

    boolean existsByLicenseKey(String licenseKey);
}