package com.alexartauddev.licenseforge.application.team.mapper;

import com.alexartauddev.licenseforge.domain.team.entity.TeamPermission;
import com.alexartauddev.licenseforge.web.dto.team.TeamPermissionDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface TeamPermissionMapper {
    TeamPermissionMapper INSTANCE = Mappers.getMapper(TeamPermissionMapper.class);

    TeamPermissionDTO toDTO(TeamPermission teamPermission);

    TeamPermission toEntity(TeamPermissionDTO dto);
}