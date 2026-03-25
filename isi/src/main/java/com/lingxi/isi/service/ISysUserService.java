package com.lingxi.isi.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lingxi.isi.models.entity.SysUser;
import com.lingxi.isi.common.result.R;
import com.lingxi.isi.models.request.other.SysUserLoginRequest;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author lingxi
 * @since 2026-03-25
 */
public interface ISysUserService extends IService<SysUser> {

    R login(SysUserLoginRequest sysUserLoginRequest);
}
