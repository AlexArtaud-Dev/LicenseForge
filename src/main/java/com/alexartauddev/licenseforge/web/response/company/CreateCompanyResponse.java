package com.alexartauddev.licenseforge.web.response.company;

import com.alexartauddev.licenseforge.web.dto.company.CompanyDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateCompanyResponse {
    private CompanyDTO company;
    private String message;
}