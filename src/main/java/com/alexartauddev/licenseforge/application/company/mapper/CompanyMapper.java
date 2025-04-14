package com.alexartauddev.licenseforge.application.company.mapper;

import com.alexartauddev.licenseforge.domain.company.entity.Company;
import com.alexartauddev.licenseforge.web.dto.company.CompanyDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface CompanyMapper {
    CompanyMapper INSTANCE = Mappers.getMapper(CompanyMapper.class);

    CompanyDTO toDTO(Company company);

    Company toEntity(CompanyDTO dto);
}