package com.lingxi.isi.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lingxi.isi.common.result.R;
import com.lingxi.isi.models.dto.LoginResponseDTO;
import com.lingxi.isi.models.dto.UserAddRequest;
import com.lingxi.isi.models.dto.UserInfoDTO;
import com.lingxi.isi.models.dto.MenuDTO;
import com.lingxi.isi.models.entity.SysUser;
import com.lingxi.isi.models.request.UserListRequest;
import com.lingxi.isi.models.request.other.SysUserLoginRequest;
import com.lingxi.isi.models.request.other.SysUserRegisterRequest;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface ISysUserService extends IService<SysUser> {

    R<LoginResponseDTO> login(SysUserLoginRequest request);

    R<UserInfoDTO> getUserInfo(HttpServletRequest request);

    R<List<MenuDTO>> getRouters();

    R<Void> logout();

    R register(SysUserRegisterRequest request);

    R listUser(UserListRequest request);

    R addUser(UserAddRequest request);
}

