package com.lingxi.seo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lingxi.seo.common.result.R;
import com.lingxi.seo.models.dto.LoginResponseDTO;
import com.lingxi.seo.models.dto.UserInfoDTO;
import com.lingxi.seo.models.entity.SysUser;
import com.lingxi.seo.models.request.other.SysUserLoginRequest;
import com.lingxi.seo.models.request.other.SysUserRegisterRequest;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

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
    
    R<Void> logout();
    
    R register(SysUserRegisterRequest request);

}
