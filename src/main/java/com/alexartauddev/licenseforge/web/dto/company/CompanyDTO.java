package com.alexartauddev.licenseforge.web.dto.company;

import com.alexartauddev.licenseforge.domain.company.entity.Company;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyDTO {
    private UUID id;
    private String name;
    private String realmId;
    private int quotaApps;
    private int quotaKeysPerApp;
    private Company.PlanType planType;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private long realmsCount;
    private long usersCount;
}