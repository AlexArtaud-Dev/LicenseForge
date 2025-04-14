package com.alexartauddev.licenseforge.web.response.user;

import com.alexartauddev.licenseforge.web.dto.user.UserDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserListResponse {
    private List<UserDTO> users;
    private long total;
    private int page;
    private int size;
}
