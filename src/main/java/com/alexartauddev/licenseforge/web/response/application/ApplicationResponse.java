package com.alexartauddev.licenseforge.web.response.application;

import com.alexartauddev.licenseforge.web.dto.application.ApplicationDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationResponse {
    private ApplicationDTO application;
}