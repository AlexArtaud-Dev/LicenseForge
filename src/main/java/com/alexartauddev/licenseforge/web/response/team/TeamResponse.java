package com.alexartauddev.licenseforge.web.response.team;

import com.alexartauddev.licenseforge.web.dto.team.TeamDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeamResponse {
    private TeamDTO team;
}