package com.alexartauddev.licenseforge.web.request.company;

import com.alexartauddev.licenseforge.domain.company.entity.Company;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateCompanyRequest {
    @NotBlank(message = "Name is required")
    @Size(min = 3, max = 100, message = "Name must be between 3 and 100 characters")
    private String name;

    @NotBlank(message = "Realm ID is required")
    @Size(min = 3, max = 50, message = "Realm ID must be between 3 and 50 characters")
    private String realmId;

    @Min(value = 0, message = "Quota apps must be at least 0")
    private int quotaApps;

    @Min(value = 0, message = "Quota keys per app must be at least 0")
    private int quotaKeysPerApp;

    @NotNull(message = "Plan type is required")
    private Company.PlanType planType;
}