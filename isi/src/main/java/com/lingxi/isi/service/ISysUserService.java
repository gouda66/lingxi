package com.lingxi.isi.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lingxi.isi.common.result.R;
import com.lingxi.isi.models.dto.*;
import com.lingxi.isi.models.request.*;
import com.lingxi.isi.models.entity.SysUser;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface ISysUserService extends IService<SysUser> {

    R<LoginResponseDTO> login(SysUserLoginRequest request);

    R<UserInfoDTO> getUserInfo(HttpServletRequest request);

    R<List<MenuDTO>> getRouters();

    R<Void> logout();

    R register(SysUserRegisterRequest request);

    R listUser(UserListRequest request);

    /**
     * 新增用户
     * @param request 用户请求参数
     * @return 操作结果
     */
    R<Void> addUser(UserRequest request);

    /**
     * 修改用户
     * @param request 用户请求参数
     * @return 操作结果
     */
    R<Void> updateUser(UserRequest request);

    R<UserDetailDTO> getUserById(Long userId);

    /**
     * 批量删除用户
     * @param userIds 用户 ID 列表（逗号分隔）
     * @return 操作结果
     */
    R<Void> deleteUsers(String userIds);

    R resetPassword(ResetPasswordRequest request);

    R<AuthRoleDTO> getUserRoles(Long userId);

    R<Void> updateAuthRole(AuthRoleRequest request);

    R downloadResume(String userId);

    R downloadInterviewReport(String userId);

    R sendEmail(String userId);
}

