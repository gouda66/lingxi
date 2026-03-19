package com.lingxi.isi.application.mapper;

import com.lingxi.isi.application.dto.UserDTO;
import com.lingxi.isi.domain.model.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * 用户 Mapper
 */
@Mapper
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    /**
     * Entity 转 DTO
     */
    @Mapping(target = "id", source = "id")
    @Mapping(target = "username", source = "username")
    @Mapping(target = "realName", source = "realName")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "phone", source = "phone")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "role", source = "role")
    @Mapping(target = "companyId", source = "companyId")
    @Mapping(target = "companyName", source = "companyName")
    @Mapping(target = "createTime", source = "createTime")
    @Mapping(target = "updateTime", source = "updateTime")
    UserDTO toDTO(User user);

    /**
     * DTO 转 Entity（用于更新操作）
     */
    @Mapping(target = "id", source = "id")
    @Mapping(target = "username", source = "username")
    @Mapping(target = "realName", source = "realName")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "phone", source = "phone")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "role", source = "role")
    @Mapping(target = "companyId", source = "companyId")
    @Mapping(target = "companyName", source = "companyName")
    @Mapping(target = "createTime", source = "createTime")
    @Mapping(target = "updateTime", source = "updateTime")
    User toEntity(UserDTO dto);
}
