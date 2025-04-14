package com.alexartauddev.licenseforge.application.team.mapper;

import com.alexartauddev.licenseforge.domain.team.entity.Team;
import com.alexartauddev.licenseforge.web.dto.team.TeamDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface TeamMapper {
    TeamMapper INSTANCE = Mappers.getMapper(TeamMapper.class);

    TeamDTO toDTO(Team team);

    Team toEntity(TeamDTO dto);
}