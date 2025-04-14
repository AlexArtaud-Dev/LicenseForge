package com.alexartauddev.licenseforge.web.controller.user;

import com.alexartauddev.licenseforge.application.user.service.UserTeamService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/user-teams")
@RequiredArgsConstructor
@Tag(name = "User Team Management", description = "API for managing user team memberships")
@SecurityRequirement(name = "bearerAuth")
public class UserTeamController {

    private final UserTeamService userTeamService;

    @Data
    public static class AddUserToTeamRequest {
        @NotNull(message = "User ID is required")
        private UUID userId;

        @NotNull(message = "Team ID is required")
        private UUID teamId;
    }

    @Data
    public static class RemoveUserFromTeamRequest {
        @NotNull(message = "User ID is required")
        private UUID userId;

        @NotNull(message = "Team ID is required")
        private UUID teamId;
    }

    @PostMapping("/add")
    @Operation(summary = "Add a user to a team",
            description = "Adds a user to a team, allowing them to be part of multiple teams")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> addUserToTeam(
            @Parameter(description = "User and team details", required = true)
            @Valid @RequestBody AddUserToTeamRequest request) {

        userTeamService.addUserToTeam(request.getUserId(), request.getTeamId());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/remove")
    @Operation(summary = "Remove a user from a team")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> removeUserFromTeam(
            @Parameter(description = "User and team details", required = true)
            @Valid @RequestBody RemoveUserFromTeamRequest request) {

        userTeamService.removeUserFromTeam(request.getUserId(), request.getTeamId());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/user/{userId}/teams")
    @Operation(summary = "Get all teams for a user")
    public ResponseEntity<List<UUID>> getUserTeams(
            @Parameter(description = "User ID", required = true)
            @PathVariable UUID userId) {

        List<UUID> teamIds = userTeamService.getTeamsForUser(userId);
        return ResponseEntity.ok(teamIds);
    }

    @GetMapping("/team/{teamId}/users")
    @Operation(summary = "Get all users in a team")
    public ResponseEntity<List<UUID>> getTeamUsers(
            @Parameter(description = "Team ID", required = true)
            @PathVariable UUID teamId) {

        List<UUID> userIds = userTeamService.getUsersInTeam(teamId);
        return ResponseEntity.ok(userIds);
    }
}