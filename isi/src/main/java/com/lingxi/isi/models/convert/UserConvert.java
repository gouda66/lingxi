package com.lingxi.isi.models.convert;

import com.lingxi.isi.models.dto.*;
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
    @Mapping(target = "id", source = "id")
    @Mapping(target = "username", source = "username")
    @Mapping(target = "nickname", source = "realName")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "phonenumber", source = "phone")
    @Mapping(target = "sex", ignore = true)
    @Mapping(target = "avatar", source = "avatarUrl")
    @Mapping(target = "userType", source = "role")
    @Mapping(target = "role", source = "role")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "loginIp", ignore = true)
    @Mapping(target = "loginDate", source = "lastLoginTime")
    @Mapping(target = "createTime", source = "createdAt")
    UserLoginDTO convertToLoginDTO(SysUser user);

    /**
     * 将 Entity 转换为登录响应 DTO
     */
    @Mapping(target = "userId", source = "id")
    @Mapping(target = "userName", source = "username")
    @Mapping(target = "nickName", expression = "java(user.getRealName() != null ? user.getRealName() : user.getUsername())")
    @Mapping(target = "avatar", source = "avatarUrl")
    @Mapping(target = "token", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "permissions", ignore = true)
    @Mapping(target = "isDefaultModifyPwd", constant = "false")
    @Mapping(target = "isPasswordExpired", constant = "false")
    LoginResponseDTO convertToLoginResponseDTO(SysUser user);

    /**
     * 将 Entity 转换为用户详细信息 DTO
     */
    @Mapping(target = "userId", source = "id")
    @Mapping(target = "userName", source = "username")
    @Mapping(target = "nickName", expression = "java(user.getRealName() != null ? user.getRealName() : user.getUsername())")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "phone", source = "phone")
    @Mapping(target = "sex", ignore = true)
    @Mapping(target = "avatar", source = "avatarUrl")
    @Mapping(target = "status", source = "status")
    UserDetailDTO convertToUserDetailDTO(SysUser user);

    /**
     * 将 Entity 转换为注册响应 DTO
     */
    @Mapping(target = "userId", source = "id")
    @Mapping(target = "userName", source = "username")
    @Mapping(target = "nickName", expression = "java(user.getRealName() != null ? user.getRealName() : user.getUsername())")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "phone", source = "phone")
    @Mapping(target = "message", constant = "注册成功")
    RegisterResponseDTO convertToRegisterResponseDTO(SysUser user);
}
