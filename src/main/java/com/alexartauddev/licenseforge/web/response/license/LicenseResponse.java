package com.alexartauddev.licenseforge.web.response.license;

import com.alexartauddev.licenseforge.web.dto.license.LicenseDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LicenseResponse {
    private LicenseDTO license;
}
