package com.alexartauddev.licenseforge.web.response.application;

import com.alexartauddev.licenseforge.web.dto.application.ApplicationDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationListResponse {
    private List<ApplicationDTO> applications;
    private long total;
    private int page;
    private int size;
}