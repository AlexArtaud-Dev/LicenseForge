package com.alexartauddev.licenseforge.infrastructure.persistence.repository.team;

import com.alexartauddev.licenseforge.domain.team.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data JPA repository for Team entities
 */
@Repository
interface SpringDataTeamRepository extends JpaRepository<Team, UUID> {

    List<Team> findByCompanyId(UUID companyId);

    Optional<Team> findByNameAndCompanyId(String name, UUID companyId);

    long countByCompanyId(UUID companyId);
}