package com.alexartauddev.licenseforge.application.user.service;

import com.alexartauddev.licenseforge.domain.user.entity.User;
import com.alexartauddev.licenseforge.web.dto.user.UserDTO;
import com.alexartauddev.licenseforge.web.request.user.ChangePasswordRequest;
import com.alexartauddev.licenseforge.web.request.user.CreateUserRequest;
import com.alexartauddev.licenseforge.web.request.user.UpdateUserRequest;

import java.util.List;
import java.util.UUID;

public interface UserService {
    UserDTO createUser(CreateUserRequest request);

    UserDTO getUserById(UUID id);

    UserDTO getUserByEmail(String email);

    List<UserDTO> getAllUsers(int page, int size);

    List<UserDTO> getUsersByCompanyId(UUID companyId, int page, int size);

    List<UserDTO> getUsersByTeamId(UUID teamId, int page, int size);

    List<UserDTO> getUsersByRoleAndCompanyId(User.Role role, UUID companyId, int page, int size);

    UserDTO updateUser(UUID id, UpdateUserRequest request);

    void changePassword(UUID id, ChangePasswordRequest request);

    void deleteUser(UUID id);

    boolean existsByEmail(String email);

    long countByCompanyId(UUID companyId);

    long countByTeamId(UUID teamId);
}
