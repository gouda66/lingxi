package com.lingxi.seo.controller;

import com.lingxi.seo.common.result.R;
import com.lingxi.seo.common.util.ValidateCodeUtils;
import com.lingxi.seo.models.dto.LoginResponseDTO;
import com.lingxi.seo.models.dto.UserInfoDTO;
import com.lingxi.seo.models.request.other.SysUserLoginRequest;
import com.lingxi.seo.models.request.other.SysUserRegisterRequest;
import com.lingxi.seo.service.ISysUserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 用户表 前端控制器
 * </p>
 *
 * @author lingxi
 * @since 2026-03-25
 */
@Slf4j
@RestController
@RequestMapping("/seo")
public class SysUserController {

    private final ISysUserService userService;
    private final ValidateCodeUtils validateCodeUtils;
    
    public SysUserController(ISysUserService userService, ValidateCodeUtils validateCodeUtils) {
        this.userService = userService;
        this.validateCodeUtils = validateCodeUtils;
    }


    @PostMapping("/login")
    public R<LoginResponseDTO> login(@RequestBody SysUserLoginRequest request, HttpServletRequest httpRequest) {
        boolean valid = request.isValid();
        if (!valid) {
            return R.error("用户名或密码不能为空");
        }
        
        // 设置设备信息
        if (request.getDeviceIp() == null || request.getDeviceIp().isEmpty()) {
            request.setDeviceIp(httpRequest.getRemoteAddr());
        }
        
        return userService.login(request);
    }
    
    @GetMapping("/getInfo")
    public R<UserInfoDTO> getInfo(HttpServletRequest request) {
        return userService.getUserInfo(request);
    }

    @PostMapping("/logout")
    public R<Void> logout() {
        return userService.logout();
    }
    
    @PostMapping("/register")
    public R register(@RequestBody SysUserRegisterRequest request) {
        boolean valid = request.isValid();
        if (!valid) {
            return R.error("用户名或密码不能为空");
        }

        if (!request.isPasswordMatch()) {
            return R.error("两次输入的密码不一致");
        }
        return userService.register(request);
    }

    @GetMapping("/captchaImage")
    public R captchaImage() {
        try {
            Map<String, Object> result = validateCodeUtils.generateCaptchaImage();
            return R.success(result);
        } catch (Exception e) {
            return R.error("验证码生成失败：" + e.getMessage());
        }
    }

}
