package com.alexartauddev.licenseforge.web.dto.user;

import com.alexartauddev.licenseforge.domain.user.entity.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
public class UserDTO {
    private UUID id;
    private String email;
    private String firstName;
    private String lastName;
    @JsonIgnore
    private String passwordHash;
    private User.Role role;
    private UUID companyId;
    private UUID teamId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String companyName;
    private String teamName;
}