package com.lingxi.isi.service;

/**
 * <p>
 * 密码分段加密使用说明
 * </p>
 * 
 * <h2>一、加密流程</h2>
 * <pre>
 * 1. 用户注册/修改密码时：
 *    String encryptedPassword = PasswordSegmentUtils.encrypt("myPassword123");
 *    user.setPassword(encryptedPassword);
 *    userService.save(user);
 * 
 * 2. 用户登录时：
 *    boolean verified = PasswordSegmentUtils.decryptAndVerify(
 *        user.getPassword(),      // 数据库中存储的加密密码
 *        loginRequest.getPassword() // 用户输入的明文密码
 *    );
 *    if (verified) {
 *        // 登录成功
 *    }
 * </pre>
 * 
 * <h2>二、密钥管理</h2>
 * <pre>
 * 1. 首次启动系统，会在日志中输出自动生成的 4 个主密钥
 * 2. 复制这 4 个密钥到配置文件 application.yml 或单独的配置中心
 * 3. 重启系统后，会从配置文件加载持久化密钥
 * 
 * 配置示例（application.yml）：
 * password:
 *   master-key-1: "Base64 编码的密钥 1"
 *   master-key-2: "Base64 编码的密钥 2"
 *   master-key-3: "Base64 编码的密钥 3"
 *   master-key-4: "Base64 编码的密钥 4"
 * </pre>
 * 
 * <h2>三、安全建议</h2>
 * <ul>
 *     <li>生产环境必须使用配置文件中的持久化密钥</li>
 *     <li>定期更换密钥（建议每 6 个月）</li>
 *     <li>密钥应存储在安全的密钥管理系统中</li>
 *     <li>不同环境使用不同的密钥</li>
 *     <li>密钥访问需要审计日志</li>
 * </ul>
 * 
 * @author lingxi
 * @since 2026-03-25
 */
public interface PasswordEncryptionGuide {
}
