package com.lingxi.isi.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lingxi.isi.common.result.R;
import com.lingxi.isi.models.dto.LoginResponseDTO;
import com.lingxi.isi.models.dto.RegisterResponseDTO;
import com.lingxi.isi.models.dto.UserInfoDTO;
import com.lingxi.isi.models.dto.MenuDTO;
import com.lingxi.isi.models.entity.SysUser;
import com.lingxi.isi.models.request.other.SysUserLoginRequest;
import com.lingxi.isi.models.request.other.SysUserRegisterRequest;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author lingxi
 * @since 2026-03-25
 */
public interface ISysUserService extends IService<SysUser> {
    
    R<LoginResponseDTO> login(SysUserLoginRequest request);
    
    R<UserInfoDTO> getUserInfo(HttpServletRequest request);
    
    R<List<MenuDTO>> getRouters();
    
    R<Void> logout();
    
    R register(SysUserRegisterRequest request);

}
