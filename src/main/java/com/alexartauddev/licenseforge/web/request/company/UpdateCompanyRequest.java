package com.alexartauddev.licenseforge.web.request.company;

import com.alexartauddev.licenseforge.domain.company.entity.Company;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCompanyRequest {
    @Size(min = 3, max = 100, message = "Name must be between 3 and 100 characters")
    private String name;

    @Min(value = 0, message = "Quota apps must be at least 0")
    private Integer quotaApps;

    @Min(value = 0, message = "Quota keys per app must be at least 0")
    private Integer quotaKeysPerApp;

    private Company.PlanType planType;
}