package com.alexartauddev.licenseforge.web.request.team;

import com.alexartauddev.licenseforge.domain.team.entity.TeamPermission;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateTeamPermissionRequest {
    @NotNull(message = "Permission type is required")
    private TeamPermission.PermissionType permissionType;
}