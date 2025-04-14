// Path: src/main/java/com/alexartauddev/licenseforge/domain/team/repository/TeamRepository.java
package com.alexartauddev.licenseforge.domain.team.repository;

import com.alexartauddev.licenseforge.domain.team.entity.Team;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for Team entities
 * This is a technology-agnostic interface in the domain layer
 */
public interface TeamRepository {

    /**
     * Save a team
     */
    Team save(Team team);

    /**
     * Find a team by its ID
     */
    Optional<Team> findById(UUID id);

    /**
     * Find all teams
     */
    List<Team> findAll();

    /**
     * Find all teams that belong to a specific company
     */
    List<Team> findByCompanyId(UUID companyId);

    /**
     * Find a team by its name and company ID
     */
    Optional<Team> findByNameAndCompanyId(String name, UUID companyId);

    /**
     * Count teams by company ID
     */
    long countByCompanyId(UUID companyId);

    /**
     * Delete a team
     */
    void delete(Team team);
}