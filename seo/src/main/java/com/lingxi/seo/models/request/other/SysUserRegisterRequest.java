package com.lingxi.seo.models.request.other;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class SysUserRegisterRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    
    /**
     * 用户名
     */
    private String username;
    
    /**
     * 密码
     */
    private String password;
    
    /**
     * 确认密码
     */
    private String confirmPassword;
    
    /**
     * 邮箱
     */
    private String email;
    
    /**
     * 手机号
     */
    private String phone;
    
    /**
     * 真实姓名
     */
    private String realName;
    
    /**
     * 验证码
     */
    private String code;
    
    /**
     * UUID（用于验证码验证）
     */
    private String uuid;
    
    /**
     * 验证基本信息是否完整
     */
    public boolean isValid() {
        return username != null && !username.trim().isEmpty()
            && password != null && !password.trim().isEmpty()
            && confirmPassword != null && !confirmPassword.trim().isEmpty();
    }
    
    /**
     * 验证两次密码是否一致
     */
    public boolean isPasswordMatch() {
        return password != null && password.equals(confirmPassword);
    }

}
