package com.lingxi.isi.models.convert;

import com.lingxi.isi.models.dto.UserLoginDTO;
import com.lingxi.isi.models.entity.SysUser;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserConvert {
    UserConvert INSTANCE = Mappers.getMapper(UserConvert.class);

    /**
     * 将 Entity 转换为 Login DTO
     */
    @Mapping(target = "phonenumber", source = "phone")
    @Mapping(target = "avatar", source = "avatarUrl")
    @Mapping(target = "loginIp", source = "lastLoginTime")
    @Mapping(target = "loginDate", source = "lastLoginTime")
    @Mapping(target = "createTime", source = "createdAt")
    UserLoginDTO convertToLoginDTO(SysUser user);
}
