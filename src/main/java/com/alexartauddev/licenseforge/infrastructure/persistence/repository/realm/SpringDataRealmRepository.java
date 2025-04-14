package com.alexartauddev.licenseforge.infrastructure.persistence.repository.realm;

import com.alexartauddev.licenseforge.domain.realm.entity.Realm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data JPA repository for Realm entities
 */
@Repository
interface SpringDataRealmRepository extends JpaRepository<Realm, UUID> {

    List<Realm> findByCompanyId(UUID companyId);

    Optional<Realm> findByNameAndCompanyId(String name, UUID companyId);

    long countByCompanyId(UUID companyId);
}
