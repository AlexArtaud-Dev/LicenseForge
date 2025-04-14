package com.alexartauddev.licenseforge.web.request.team;

import com.alexartauddev.licenseforge.domain.team.entity.TeamPermission;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateTeamPermissionRequest {
    @NotNull(message = "Team ID is required")
    private UUID teamId;

    @NotNull(message = "Application ID is required")
    private UUID appId;

    @NotNull(message = "Permission type is required")
    private TeamPermission.PermissionType permissionType;
}