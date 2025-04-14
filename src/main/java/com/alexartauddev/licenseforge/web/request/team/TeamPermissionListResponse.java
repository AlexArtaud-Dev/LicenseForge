package com.alexartauddev.licenseforge.web.request.team;

import com.alexartauddev.licenseforge.web.dto.team.TeamPermissionDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeamPermissionListResponse {
    private List<TeamPermissionDTO> teamPermissions;
    private long total;
}
