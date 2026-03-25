package com.lingxi.isi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lingxi.isi.common.result.R;
import com.lingxi.isi.mapper.SysUserMapper;
import com.lingxi.isi.models.convert.UserConvert;
import com.lingxi.isi.models.dto.UserLoginDTO;
import com.lingxi.isi.models.entity.SysUser;
import com.lingxi.isi.models.request.other.SysUserLoginRequest;
import com.lingxi.isi.service.ISysUserService;
import com.lingxi.isi.utils.PasswordSegmentUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author lingxi
 * @since 2026-03-25
 */
@Slf4j
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements ISysUserService {
    
    private final SysUserMapper sysUserMapper;
    
    public SysUserServiceImpl(SysUserMapper sysUserMapper) {
        this.sysUserMapper = sysUserMapper;
    }

    @Override
    public R login(SysUserLoginRequest request) {
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUser::getUsername, request.getUsername());
        queryWrapper.eq(SysUser::getDeleted, 0);
        SysUser user = sysUserMapper.selectOne(queryWrapper);
        
        if (user == null) {
            return R.error("用户名或密码错误");
        }

        if (!PasswordSegmentUtils.validatePassword(user.getPassword(), request.getPassword())) {
            return R.error("用户名或密码错误");
        }

        UserLoginDTO loginDTO = UserConvert.INSTANCE.convertToLoginDTO(user);
        return R.success(loginDTO);
    }
    


}
