package com.alexartauddev.licenseforge.web.response.team;

import com.alexartauddev.licenseforge.web.dto.team.TeamDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeamListResponse {
    private List<TeamDTO> teams;
    private long total;
    private int page;
    private int size;
}
