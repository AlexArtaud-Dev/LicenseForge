package com.alexartauddev.licenseforge.infrastructure.persistence.repository.license;

import com.alexartauddev.licenseforge.domain.license.entity.License;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data JPA repository for License entities
 */
@Repository
interface SpringDataLicenseRepository extends JpaRepository<License, UUID> {

    Optional<License> findByLicenseKey(String licenseKey);

    List<License> findByAppId(UUID appId);

    List<License> findByCustomerId(String customerId);

    @Query("SELECT l FROM License l WHERE l.appId = :appId AND l.revoked = false AND (l.expiresAt IS NULL OR l.expiresAt > :now)")
    List<License> findActiveByAppId(@Param("appId") UUID appId, @Param("now") LocalDateTime now);

    @Query("SELECT l FROM License l WHERE l.appId = :appId AND l.revoked = false AND l.expiresAt BETWEEN :start AND :end")
    List<License> findExpiringLicenses(@Param("appId") UUID appId, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT COUNT(l) FROM License l WHERE l.appId = :appId AND l.revoked = false AND (l.expiresAt IS NULL OR l.expiresAt > :now)")
    long countActiveByAppId(@Param("appId") UUID appId, @Param("now") LocalDateTime now);
}