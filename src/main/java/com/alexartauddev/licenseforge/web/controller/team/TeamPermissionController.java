package com.alexartauddev.licenseforge.web.controller.team;

import com.alexartauddev.licenseforge.application.team.service.TeamPermissionService;
import com.alexartauddev.licenseforge.domain.team.entity.TeamPermission;
import com.alexartauddev.licenseforge.web.dto.team.TeamPermissionDTO;
import com.alexartauddev.licenseforge.web.request.team.CreateTeamPermissionRequest;
import com.alexartauddev.licenseforge.web.request.team.TeamPermissionListResponse;
import com.alexartauddev.licenseforge.web.request.team.UpdateTeamPermissionRequest;
import com.alexartauddev.licenseforge.web.response.team.TeamPermissionResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/team-permissions")
@RequiredArgsConstructor
@Tag(name = "Team Permissions", description = "Team permission management API")
public class TeamPermissionController {

    private final TeamPermissionService teamPermissionService;

    @PostMapping
    @Operation(summary = "Create a new team permission")
    public ResponseEntity<TeamPermissionResponse> createTeamPermission(@Valid @RequestBody CreateTeamPermissionRequest request) {
        TeamPermissionDTO teamPermission = teamPermissionService.createTeamPermission(request);
        return new ResponseEntity<>(new TeamPermissionResponse(teamPermission), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get team permission by ID")
    public ResponseEntity<TeamPermissionResponse> getTeamPermissionById(
            @Parameter(description = "Team permission ID", required = true)
            @PathVariable UUID id) {
        TeamPermissionDTO teamPermission = teamPermissionService.getTeamPermissionById(id);
        return ResponseEntity.ok(new TeamPermissionResponse(teamPermission));
    }

    @GetMapping("/team/{teamId}/app/{appId}")
    @Operation(summary = "Get team permission by team ID and application ID")
    public ResponseEntity<TeamPermissionResponse> getTeamPermissionByTeamIdAndAppId(
            @Parameter(description = "Team ID", required = true)
            @PathVariable UUID teamId,
            @Parameter(description = "Application ID", required = true)
            @PathVariable UUID appId) {
        TeamPermissionDTO teamPermission = teamPermissionService.getTeamPermissionByTeamIdAndAppId(teamId, appId);
        return ResponseEntity.ok(new TeamPermissionResponse(teamPermission));
    }

    @GetMapping("/team/{teamId}")
    @Operation(summary = "Get team permissions by team ID")
    public ResponseEntity<TeamPermissionListResponse> getTeamPermissionsByTeamId(
            @Parameter(description = "Team ID", required = true)
            @PathVariable UUID teamId) {
        List<TeamPermissionDTO> teamPermissions = teamPermissionService.getTeamPermissionsByTeamId(teamId);
        return ResponseEntity.ok(new TeamPermissionListResponse(teamPermissions, teamPermissions.size()));
    }

    @GetMapping("/app/{appId}")
    @Operation(summary = "Get team permissions by application ID")
    public ResponseEntity<TeamPermissionListResponse> getTeamPermissionsByAppId(
            @Parameter(description = "Application ID", required = true)
            @PathVariable UUID appId) {
        List<TeamPermissionDTO> teamPermissions = teamPermissionService.getTeamPermissionsByAppId(appId);
        return ResponseEntity.ok(new TeamPermissionListResponse(teamPermissions, teamPermissions.size()));
    }

    @GetMapping("/team/{teamId}/type/{permissionType}")
    @Operation(summary = "Get team permissions by team ID and permission type")
    public ResponseEntity<TeamPermissionListResponse> getTeamPermissionsByTeamIdAndPermissionType(
            @Parameter(description = "Team ID", required = true)
            @PathVariable UUID teamId,
            @Parameter(description = "Permission type", required = true)
            @PathVariable TeamPermission.PermissionType permissionType) {
        List<TeamPermissionDTO> teamPermissions = teamPermissionService.getTeamPermissionsByTeamIdAndPermissionType(teamId, permissionType);
        return ResponseEntity.ok(new TeamPermissionListResponse(teamPermissions, teamPermissions.size()));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a team permission")
    public ResponseEntity<TeamPermissionResponse> updateTeamPermission(
            @Parameter(description = "Team permission ID", required = true)
            @PathVariable UUID id,
            @Valid @RequestBody UpdateTeamPermissionRequest request) {
        TeamPermissionDTO teamPermission = teamPermissionService.updateTeamPermission(id, request);
        return ResponseEntity.ok(new TeamPermissionResponse(teamPermission));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a team permission")
    public ResponseEntity<Void> deleteTeamPermission(
            @Parameter(description = "Team permission ID", required = true)
            @PathVariable UUID id) {
        teamPermissionService.deleteTeamPermission(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/team/{teamId}")
    @Operation(summary = "Delete all team permissions for a team")
    public ResponseEntity<Void> deleteTeamPermissionsByTeamId(
            @Parameter(description = "Team ID", required = true)
            @PathVariable UUID teamId) {
        teamPermissionService.deleteTeamPermissionsByTeamId(teamId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/app/{appId}")
    @Operation(summary = "Delete all team permissions for an application")
    public ResponseEntity<Void> deleteTeamPermissionsByAppId(
            @Parameter(description = "Application ID", required = true)
            @PathVariable UUID appId) {
        teamPermissionService.deleteTeamPermissionsByAppId(appId);
        return ResponseEntity.noContent().build();
    }
}
