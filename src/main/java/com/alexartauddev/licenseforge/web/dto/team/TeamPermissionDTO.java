package com.alexartauddev.licenseforge.web.dto.team;

import com.alexartauddev.licenseforge.domain.team.entity.TeamPermission;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeamPermissionDTO {
    private UUID id;
    private UUID teamId;
    private UUID appId;
    private TeamPermission.PermissionType permissionType;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String teamName;
    private String appName;
}