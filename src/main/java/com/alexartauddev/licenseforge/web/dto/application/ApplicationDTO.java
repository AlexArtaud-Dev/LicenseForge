package com.alexartauddev.licenseforge.web.dto.application;

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
public class ApplicationDTO {
    private UUID id;
    private String name;
    private String description;
    private UUID realmId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private long activeLicensesCount;
}