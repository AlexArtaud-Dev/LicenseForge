package com.alexartauddev.licenseforge.infrastructure.persistence.repository.license;


import com.alexartauddev.licenseforge.domain.license.entity.Activation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data JPA repository for Activation entities
 */
@Repository
interface SpringDataActivationRepository extends JpaRepository<Activation, UUID> {

    List<Activation> findByLicenseId(UUID licenseId);

    Optional<Activation> findByLicenseIdAndHardwareId(UUID licenseId, String hardwareId);

    long countByLicenseId(UUID licenseId);

    @Query("SELECT a FROM Activation a WHERE a.lastSeenAt < :threshold")
    List<Activation> findInactiveActivations(@Param("threshold") LocalDateTime threshold);

    long deleteByLicenseId(UUID licenseId);
}