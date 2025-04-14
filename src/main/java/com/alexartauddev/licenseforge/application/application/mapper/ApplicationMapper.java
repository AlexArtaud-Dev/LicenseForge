package com.alexartauddev.licenseforge.application.application.mapper;

import com.alexartauddev.licenseforge.domain.application.entity.Application;
import com.alexartauddev.licenseforge.web.dto.application.ApplicationDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ApplicationMapper {
    ApplicationMapper INSTANCE = Mappers.getMapper(ApplicationMapper.class);

    ApplicationDTO toDTO(Application application);

    Application toEntity(ApplicationDTO dto);
}
