package com.alexartauddev.licenseforge.web.controller.user;

import com.alexartauddev.licenseforge.application.user.service.UserService;
import com.alexartauddev.licenseforge.domain.user.entity.User;
import com.alexartauddev.licenseforge.web.dto.user.UserDTO;
import com.alexartauddev.licenseforge.web.request.user.ChangePasswordRequest;
import com.alexartauddev.licenseforge.web.request.user.CreateUserRequest;
import com.alexartauddev.licenseforge.web.request.user.UpdateUserRequest;
import com.alexartauddev.licenseforge.web.response.user.UserListResponse;
import com.alexartauddev.licenseforge.web.response.user.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "User management API")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserService userService;

    @PostMapping
    @Operation(summary = "Create a new user")
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody CreateUserRequest request) {
        UserDTO user = userService.createUser(request);
        return new ResponseEntity<>(new UserResponse(user), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID")
    public ResponseEntity<UserResponse> getUserById(
            @Parameter(description = "User ID", required = true)
            @PathVariable UUID id) {
        UserDTO user = userService.getUserById(id);
        return ResponseEntity.ok(new UserResponse(user));
    }

    @GetMapping("/email/{email}")
    @Operation(summary = "Get user by email")
    public ResponseEntity<UserResponse> getUserByEmail(
            @Parameter(description = "User email", required = true)
            @PathVariable String email) {
        UserDTO user = userService.getUserByEmail(email);
        return ResponseEntity.ok(new UserResponse(user));
    }

    @GetMapping
    @Operation(summary = "Get all users")
    public ResponseEntity<UserListResponse> getAllUsers(
            @Parameter(description = "Page number (0-based)")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size")
            @RequestParam(defaultValue = "20") int size) {
        List<UserDTO> users = userService.getAllUsers(page, size);
        long total = userService.countByCompanyId(null); // Count all users
        return ResponseEntity.ok(new UserListResponse(users, total, page, size));
    }

    @GetMapping("/company/{companyId}")
    @Operation(summary = "Get users by company ID")
    public ResponseEntity<UserListResponse> getUsersByCompanyId(
            @Parameter(description = "Company ID", required = true)
            @PathVariable UUID companyId,
            @Parameter(description = "Page number (0-based)")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size")
            @RequestParam(defaultValue = "20") int size) {
        List<UserDTO> users = userService.getUsersByCompanyId(companyId, page, size);
        long total = userService.countByCompanyId(companyId);
        return ResponseEntity.ok(new UserListResponse(users, total, page, size));
    }

    @GetMapping("/team/{teamId}")
    @Operation(summary = "Get users by team ID")
    public ResponseEntity<UserListResponse> getUsersByTeamId(
            @Parameter(description = "Team ID", required = true)
            @PathVariable UUID teamId,
            @Parameter(description = "Page number (0-based)")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size")
            @RequestParam(defaultValue = "20") int size) {
        List<UserDTO> users = userService.getUsersByTeamId(teamId, page, size);
        long total = userService.countByTeamId(teamId);
        return ResponseEntity.ok(new UserListResponse(users, total, page, size));
    }

    @GetMapping("/company/{companyId}/role/{role}")
    @Operation(summary = "Get users by role and company ID")
    public ResponseEntity<UserListResponse> getUsersByRoleAndCompanyId(
            @Parameter(description = "Company ID", required = true)
            @PathVariable UUID companyId,
            @Parameter(description = "User role", required = true)
            @PathVariable User.Role role,
            @Parameter(description = "Page number (0-based)")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size")
            @RequestParam(defaultValue = "20") int size) {
        List<UserDTO> users = userService.getUsersByRoleAndCompanyId(role, companyId, page, size);
        return ResponseEntity.ok(new UserListResponse(users, users.size(), page, size));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a user")
    public ResponseEntity<UserResponse> updateUser(
            @Parameter(description = "User ID", required = true)
            @PathVariable UUID id,
            @Valid @RequestBody UpdateUserRequest request) {
        UserDTO user = userService.updateUser(id, request);
        return ResponseEntity.ok(new UserResponse(user));
    }

    @PutMapping("/{id}/password")
    @Operation(summary = "Change user password")
    public ResponseEntity<Void> changePassword(
            @Parameter(description = "User ID", required = true)
            @PathVariable UUID id,
            @Valid @RequestBody ChangePasswordRequest request) {
        userService.changePassword(id, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a user")
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "User ID", required = true)
            @PathVariable UUID id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/exists/{email}")
    @Operation(summary = "Check if email is already registered")
    public ResponseEntity<Boolean> existsByEmail(
            @Parameter(description = "Email to check", required = true)
            @PathVariable String email) {
        boolean exists = userService.existsByEmail(email);
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/company/{companyId}/count")
    @Operation(summary = "Count users by company ID")
    public ResponseEntity<Long> countByCompanyId(
            @Parameter(description = "Company ID", required = true)
            @PathVariable UUID companyId) {
        long count = userService.countByCompanyId(companyId);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/team/{teamId}/count")
    @Operation(summary = "Count users by team ID")
    public ResponseEntity<Long> countByTeamId(
            @Parameter(description = "Team ID", required = true)
            @PathVariable UUID teamId) {
        long count = userService.countByTeamId(teamId);
        return ResponseEntity.ok(count);
    }
}
