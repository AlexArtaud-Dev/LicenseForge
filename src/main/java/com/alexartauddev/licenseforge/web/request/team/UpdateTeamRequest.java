package com.alexartauddev.licenseforge.web.request.team;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateTeamRequest {
    @Size(min = 3, max = 100, message = "Name must be between 3 and 100 characters")
    private String name;
}
