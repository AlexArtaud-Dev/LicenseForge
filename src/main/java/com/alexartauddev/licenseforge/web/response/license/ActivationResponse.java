package com.alexartauddev.licenseforge.web.response.license;

import com.alexartauddev.licenseforge.web.dto.license.ActivationDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivationResponse {
    private ActivationDTO activation;
}
