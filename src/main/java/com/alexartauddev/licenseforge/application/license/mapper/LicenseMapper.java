package com.alexartauddev.licenseforge.application.license.mapper;

import com.alexartauddev.licenseforge.domain.license.entity.Activation;
import com.alexartauddev.licenseforge.domain.license.entity.License;
import com.alexartauddev.licenseforge.web.dto.license.ActivationDTO;
import com.alexartauddev.licenseforge.web.dto.license.LicenseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface LicenseMapper {

    LicenseMapper INSTANCE = Mappers.getMapper(LicenseMapper.class);

    @Mapping(target = "expired", expression = "java(license.isExpired())")
    LicenseDTO toDTO(License license);

    ActivationDTO toDTO(Activation activation);

    License toEntity(LicenseDTO dto);

    Activation toEntity(ActivationDTO dto);
}