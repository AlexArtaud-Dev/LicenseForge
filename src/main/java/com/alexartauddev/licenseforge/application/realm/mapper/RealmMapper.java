package com.alexartauddev.licenseforge.application.realm.mapper;

import com.alexartauddev.licenseforge.domain.realm.entity.Realm;
import com.alexartauddev.licenseforge.web.dto.realm.RealmDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface RealmMapper {
    RealmMapper INSTANCE = Mappers.getMapper(RealmMapper.class);

    RealmDTO toDTO(Realm realm);

    Realm toEntity(RealmDTO dto);
}