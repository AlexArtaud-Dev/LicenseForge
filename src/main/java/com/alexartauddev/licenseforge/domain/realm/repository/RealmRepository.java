// Path: src/main/java/com/alexartauddev/licenseforge/domain/realm/repository/RealmRepository.java
package com.alexartauddev.licenseforge.domain.realm.repository;


import com.alexartauddev.licenseforge.domain.realm.entity.Realm;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for Realm entities
 * This is a technology-agnostic interface in the domain layer
 */
public interface RealmRepository {

    /**
     * Save a realm
     */
    Realm save(Realm realm);

    /**
     * Find a realm by its ID
     */
    Optional<Realm> findById(UUID id);

    /**
     * Find all realms
     */
    List<Realm> findAll();

    /**
     * Find all realms that belong to a specific company
     */
    List<Realm> findByCompanyId(UUID companyId);

    /**
     * Find a realm by its name and company ID
     */
    Optional<Realm> findByNameAndCompanyId(String name, UUID companyId);

    /**
     * Count realms by company ID
     */
    long countByCompanyId(UUID companyId);

    /**
     * Delete a realm
     */
    void delete(Realm realm);
}