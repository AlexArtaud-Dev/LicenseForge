package com.alexartauddev.licenseforge.web.response.company;

import com.alexartauddev.licenseforge.web.dto.company.CompanyDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyListResponse {
    private List<CompanyDTO> companies;
    private long total;
    private int page;
    private int size;
}