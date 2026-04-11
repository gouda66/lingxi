package com.lingxi.isi.models.request;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class SysUserLoginRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    
    /**
     * 用户名
     */
    private String userName;
    
    /**
     * 密码
     */
    private String password;
    
    /**
     * 验证码
     */
    private String code;
    
    /**
     * UUID（用于验证码验证）
     */
    private String uuid;
    
    /**
     * 设备 IP
     */
    private String deviceIp;
    
    /**
     * 设备类型：PC/MOBILE/TABLET
     */
    private String deviceType;

    /**
     * 验证基本信息是否完整
     */
    public boolean isValid() {
        return userName != null && !userName.trim().isEmpty()
            && password != null && !password.trim().isEmpty();
    }
    
    /**
     * 是否需要验证码
     */
    public boolean requiresCaptcha() {
        return code != null && !code.trim().isEmpty()
            && uuid != null && !uuid.trim().isEmpty();
    }
    
    /**
     * 是否是 PC 设备
     */
    public boolean isPcDevice() {
        return "PC".equalsIgnoreCase(this.deviceType);
    }
    
    /**
     * 是否是移动设备
     */
    public boolean isMobileDevice() {
        return "MOBILE".equalsIgnoreCase(this.deviceType);
    }
    
    /**
     * 清理敏感信息（用于日志输出）
     */
    public void sanitizeForLogging() {
        // 隐藏密码：保留原始长度的前缀，其余用 * 替换
        if (this.password != null && !this.password.isEmpty()) {
            int length = this.password.length();
            int keepLength = Math.min(2, length); // 保留前 2 位
            String mask = "*".repeat(Math.max(0, length - keepLength));
            this.password = this.password.substring(0, keepLength) + mask;
        }
        
        // 隐藏验证码：全部用 * 替换
        if (this.code != null && !this.code.isEmpty()) {
            this.code = "*".repeat(this.code.length());
        }
    }

    /**
     * 复制请求对象（用于日志脱敏）
     */
    private SysUserLoginRequest cloneRequest(SysUserLoginRequest original) {
        SysUserLoginRequest clone = new SysUserLoginRequest();
        clone.setUserName(original.getUserName());
        clone.setPassword(original.getPassword());
        clone.setCode(original.getCode());
        clone.setUuid(original.getUuid());
        clone.setDeviceIp(original.getDeviceIp());
        clone.setDeviceType(original.getDeviceType());
        return clone;
    }
}
