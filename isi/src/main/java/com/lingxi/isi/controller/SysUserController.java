package com.lingxi.isi.controller;

import com.lingxi.isi.common.result.R;
import com.lingxi.isi.models.request.other.SysUserLoginRequest;
import com.lingxi.isi.service.ISysUserService;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 用户表 前端控制器
 * </p>
 *
 * @author lingxi
 * @since 2026-03-25
 */
@RestController
@RequestMapping("/sys/user")
public class SysUserController {

    private final ISysUserService userService;

    public SysUserController(ISysUserService userService) {
        this.userService = userService;
    }


    @PostMapping("/login")
    public R login(@RequestBody SysUserLoginRequest sysUserLoginRequest) {
        boolean valid = sysUserLoginRequest.isValid();
        if (!valid) {
            return R.error("用户名或密码不能为空");
        }
        return userService.login(sysUserLoginRequest);
    }


}
