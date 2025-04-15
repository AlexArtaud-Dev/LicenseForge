package com.alexartauddev.licenseforge.web.controller.team;

import com.alexartauddev.licenseforge.application.team.service.TeamService;
import com.alexartauddev.licenseforge.web.dto.team.TeamDTO;
import com.alexartauddev.licenseforge.web.request.team.CreateTeamRequest;
import com.alexartauddev.licenseforge.web.request.team.UpdateTeamRequest;
import com.alexartauddev.licenseforge.web.response.team.TeamListResponse;
import com.alexartauddev.licenseforge.web.response.team.TeamResponse;
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
@RequestMapping("/api/v1/teams")
@RequiredArgsConstructor
@Tag(name = "Teams", description = "Team management API")
@SecurityRequirement(name = "bearerAuth")
public class TeamController {

    private final TeamService teamService;

    @PostMapping
    @Operation(summary = "Create a new team")
    public ResponseEntity<TeamResponse> createTeam(@Valid @RequestBody CreateTeamRequest request) {
        TeamDTO team = teamService.createTeam(request);
        return new ResponseEntity<>(new TeamResponse(team), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get team by ID")
    public ResponseEntity<TeamResponse> getTeamById(
            @Parameter(description = "Team ID", required = true)
            @PathVariable UUID id) {
        TeamDTO team = teamService.getTeamById(id);
        return ResponseEntity.ok(new TeamResponse(team));
    }

    @GetMapping("/company/{companyId}")
    @Operation(summary = "Get teams by company ID")
    public ResponseEntity<TeamListResponse> getTeamsByCompanyId(
            @Parameter(description = "Company ID", required = true)
            @PathVariable UUID companyId,
            @Parameter(description = "Page number (0-based)")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size")
            @RequestParam(defaultValue = "20") int size) {
        List<TeamDTO> teams = teamService.getTeamsByCompanyId(companyId, page, size);
        long total = teamService.countByCompanyId(companyId);
        return ResponseEntity.ok(new TeamListResponse(teams, total, page, size));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a team")
    public ResponseEntity<TeamResponse> updateTeam(
            @Parameter(description = "Team ID", required = true)
            @PathVariable UUID id,
            @Valid @RequestBody UpdateTeamRequest request) {
        TeamDTO team = teamService.updateTeam(id, request);
        return ResponseEntity.ok(new TeamResponse(team));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a team")
    public ResponseEntity<Void> deleteTeam(
            @Parameter(description = "Team ID", required = true)
            @PathVariable UUID id) {
        teamService.deleteTeam(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/company/{companyId}/count")
    @Operation(summary = "Count teams by company ID")
    public ResponseEntity<Long> countByCompanyId(
            @Parameter(description = "Company ID", required = true)
            @PathVariable UUID companyId) {
        long count = teamService.countByCompanyId(companyId);
        return ResponseEntity.ok(count);
    }
}