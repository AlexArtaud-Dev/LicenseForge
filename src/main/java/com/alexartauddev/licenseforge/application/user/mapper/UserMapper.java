package com.alexartauddev.licenseforge.application.user.mapper;

import com.alexartauddev.licenseforge.domain.user.entity.User;
import com.alexartauddev.licenseforge.web.dto.user.UserDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    UserDTO toDTO(User user);

    User toEntity(UserDTO dto);
}