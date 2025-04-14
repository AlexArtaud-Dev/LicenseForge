package com.alexartauddev.licenseforge.web.dto.license;

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
public class ActivationDTO {
    private UUID id;
    private UUID licenseId;
    private String hardwareId;
    private LocalDateTime activatedAt;
    private LocalDateTime lastSeenAt;
}
