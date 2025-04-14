package com.alexartauddev.licenseforge.web.response.license;

import com.alexartauddev.licenseforge.web.dto.license.LicenseDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LicenseListResponse {
    private List<LicenseDTO> licenses;
    private long total;
    private int page;
    private int size;
}