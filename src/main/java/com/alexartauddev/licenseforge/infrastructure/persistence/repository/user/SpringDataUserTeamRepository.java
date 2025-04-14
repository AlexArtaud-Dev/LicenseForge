package com.alexartauddev.licenseforge.infrastructure.persistence.repository.user;

import com.alexartauddev.licenseforge.domain.user.entity.UserTeam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data JPA repository for UserTeam entities
 */
@Repository
interface SpringDataUserTeamRepository extends JpaRepository<UserTeam, UUID> {

    List<UserTeam> findByUserId(UUID userId);

    List<UserTeam> findByTeamId(UUID teamId);

    Optional<UserTeam> findByUserIdAndTeamId(UUID userId, UUID teamId);

    void deleteByUserIdAndTeamId(UUID userId, UUID teamId);

    boolean existsByUserId(UUID userId);

    boolean existsByTeamId(UUID teamId);
}