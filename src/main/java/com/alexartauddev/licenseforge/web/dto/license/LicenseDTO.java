package com.alexartauddev.licenseforge.web.dto.license;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LicenseDTO {
    private UUID id;
    private String licenseKey;
    private UUID appId;
    private String customerId;
    private LocalDateTime expiresAt;
    private int maxActivations;
    private boolean revoked;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Set<String> hardwareIds;
    private boolean expired;
    private long activationsCount; // Changed from int to long
}