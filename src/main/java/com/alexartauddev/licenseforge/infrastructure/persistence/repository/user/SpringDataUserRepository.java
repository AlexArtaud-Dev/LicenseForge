package com.alexartauddev.licenseforge.infrastructure.persistence.repository.user;

import com.alexartauddev.licenseforge.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data JPA repository for User entities
 */
@Repository
interface SpringDataUserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmail(String email);

    List<User> findByCompanyId(UUID companyId);

    List<User> findByTeamId(UUID teamId);

    List<User> findByRoleAndCompanyId(User.Role role, UUID companyId);

    boolean existsByEmail(String email);

    long countByCompanyId(UUID companyId);

    long countByTeamId(UUID teamId);
}