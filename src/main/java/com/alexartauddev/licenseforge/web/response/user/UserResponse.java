package com.alexartauddev.licenseforge.web.response.user;

import com.alexartauddev.licenseforge.web.dto.user.UserDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private UserDTO user;
}
