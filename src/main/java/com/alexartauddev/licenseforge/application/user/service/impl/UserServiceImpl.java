package com.alexartauddev.licenseforge.application.user.service.impl;

import com.alexartauddev.licenseforge.application.user.mapper.UserMapper;
import com.alexartauddev.licenseforge.application.user.service.PasswordService;
import com.alexartauddev.licenseforge.application.user.service.UserService;
import com.alexartauddev.licenseforge.domain.company.repository.CompanyRepository;
import com.alexartauddev.licenseforge.domain.team.repository.TeamRepository;
import com.alexartauddev.licenseforge.domain.user.entity.User;
import com.alexartauddev.licenseforge.domain.user.repository.UserRepository;
import com.alexartauddev.licenseforge.web.dto.user.UserDTO;
import com.alexartauddev.licenseforge.web.exception.company.CompanyNotFoundException;
import com.alexartauddev.licenseforge.web.exception.team.TeamNotFoundException;
import com.alexartauddev.licenseforge.web.exception.user.DuplicateEmailException;
import com.alexartauddev.licenseforge.web.exception.user.PasswordMismatchException;
import com.alexartauddev.licenseforge.web.exception.user.PasswordsDoNotMatchException;
import com.alexartauddev.licenseforge.web.exception.user.UserNotFoundException;
import com.alexartauddev.licenseforge.web.request.user.ChangePasswordRequest;
import com.alexartauddev.licenseforge.web.request.user.CreateUserRequest;
import com.alexartauddev.licenseforge.web.request.user.UpdateUserRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final TeamRepository teamRepository;
    private final PasswordService passwordService;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public UserDTO createUser(CreateUserRequest request) {
        // Check if email is already registered
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateEmailException(request.getEmail());
        }

        // Verify company exists
        companyRepository.findById(request.getCompanyId())
                .orElseThrow(() -> CompanyNotFoundException.withId(request.getCompanyId()));

        // Verify team exists if provided
        if (request.getTeamId() != null) {
            teamRepository.findById(request.getTeamId())
                    .orElseThrow(() -> new TeamNotFoundException("Team not found with id: " + request.getTeamId()));
        }

        // Hash the password
        String hashedPassword = passwordService.hashPassword(request.getPassword());

        User user = User.builder()
                .email(request.getEmail())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .passwordHash(hashedPassword)
                .role(request.getRole())
                .companyId(request.getCompanyId())
                .teamId(request.getTeamId())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        User savedUser = userRepository.save(user);

        return enrichUserDTO(userMapper.toDTO(savedUser));
    }

    @Override
    @Transactional(readOnly = true)
    public UserDTO getUserById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> UserNotFoundException.withId(id));

        return enrichUserDTO(userMapper.toDTO(user));
    }

    @Override
    @Transactional(readOnly = true)
    public UserDTO getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> UserNotFoundException.withEmail(email));

        return enrichUserDTO(userMapper.toDTO(user));
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDTO> getAllUsers(int page, int size) {
        return userRepository.findAll().stream()
                .skip((long) page * size)
                .limit(size)
                .map(user -> enrichUserDTO(userMapper.toDTO(user)))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDTO> getUsersByCompanyId(UUID companyId, int page, int size) {
        return userRepository.findByCompanyId(companyId).stream()
                .skip((long) page * size)
                .limit(size)
                .map(user -> enrichUserDTO(userMapper.toDTO(user)))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDTO> getUsersByTeamId(UUID teamId, int page, int size) {
        return userRepository.findByTeamId(teamId).stream()
                .skip((long) page * size)
                .limit(size)
                .map(user -> enrichUserDTO(userMapper.toDTO(user)))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDTO> getUsersByRoleAndCompanyId(User.Role role, UUID companyId, int page, int size) {
        return userRepository.findByRoleAndCompanyId(role, companyId).stream()
                .skip((long) page * size)
                .limit(size)
                .map(user -> enrichUserDTO(userMapper.toDTO(user)))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UserDTO updateUser(UUID id, UpdateUserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> UserNotFoundException.withId(id));

        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new DuplicateEmailException(request.getEmail());
            }
            user.setEmail(request.getEmail());
        }

        if (request.getFirstName() != null) {
            user.setFirstName(request.getFirstName());
        }

        if (request.getLastName() != null) {
            user.setLastName(request.getLastName());
        }

        if (request.getRole() != null) {
            user.setRole(request.getRole());
        }

        if (request.getTeamId() != null && !request.getTeamId().equals(user.getTeamId())) {
            teamRepository.findById(request.getTeamId())
                    .orElseThrow(() -> new TeamNotFoundException("Team not found with id: " + request.getTeamId()));
            user.setTeamId(request.getTeamId());
        }

        user.setUpdatedAt(LocalDateTime.now());

        User updatedUser = userRepository.save(user);

        return enrichUserDTO(userMapper.toDTO(updatedUser));
    }

    @Override
    @Transactional
    public void changePassword(UUID id, ChangePasswordRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> UserNotFoundException.withId(id));

        // Verify current password
        if (!passwordService.verifyPassword(request.getCurrentPassword(), user.getPasswordHash())) {
            throw new PasswordMismatchException();
        }

        // Verify new password and confirm password match
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new PasswordsDoNotMatchException();
        }

        // Hash and update new password
        String hashedPassword = passwordService.hashPassword(request.getNewPassword());
        user.setPasswordHash(hashedPassword);
        user.setUpdatedAt(LocalDateTime.now());

        userRepository.save(user);
    }

    @Override
    @Transactional
    public void deleteUser(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> UserNotFoundException.withId(id));

        userRepository.delete(user);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public long countByCompanyId(UUID companyId) {
        return userRepository.countByCompanyId(companyId);
    }

    @Override
    @Transactional(readOnly = true)
    public long countByTeamId(UUID teamId) {
        return userRepository.countByTeamId(teamId);
    }

    // Helper method to enrich DTO with company and team names
    private UserDTO enrichUserDTO(UserDTO dto) {
        if (dto.getCompanyId() != null) {
            companyRepository.findById(dto.getCompanyId()).ifPresent(company ->
                    dto.setCompanyName(company.getName()));
        }

        if (dto.getTeamId() != null) {
            teamRepository.findById(dto.getTeamId()).ifPresent(team ->
                    dto.setTeamName(team.getName()));
        }

        return dto;
    }
}