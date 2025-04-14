package com.alexartauddev.licenseforge.domain.user.repository;

import com.alexartauddev.licenseforge.domain.user.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for User entities
 * This is a technology-agnostic interface in the domain layer
 */
public interface UserRepository {

    /**
     * Save a user
     */
    User save(User user);

    /**
     * Find a user by their ID
     */
    Optional<User> findById(UUID id);

    /**
     * Find all users
     */
    List<User> findAll();

    /**
     * Find a user by their email address
     */
    Optional<User> findByEmail(String email);

    /**
     * Find all users that belong to a specific company
     */
    List<User> findByCompanyId(UUID companyId);

    /**
     * Find all users that belong to a specific team
     */
    List<User> findByTeamId(UUID teamId);

    /**
     * Find all users with a specific role in a company
     */
    List<User> findByRoleAndCompanyId(User.Role role, UUID companyId);

    /**
     * Check if an email address is already registered
     */
    boolean existsByEmail(String email);

    /**
     * Count users by company ID
     */
    long countByCompanyId(UUID companyId);

    /**
     * Count users by team ID
     */
    long countByTeamId(UUID teamId);

    /**
     * Delete a user
     */
    void delete(User user);
}