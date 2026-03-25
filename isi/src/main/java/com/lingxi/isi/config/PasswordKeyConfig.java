package com.lingxi.isi.config;

import com.lingxi.isi.utils.PasswordSegmentUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;

/**
 * <p>
 * 密码密钥配置管理
 * </p>
 *
 * @author lingxi
 * @since 2026-03-25
 */
@Slf4j
@Configuration
public class PasswordKeyConfig {
    
    @Value("${password.master-key-1:}")
    private String masterKey1;
    
    @Value("${password.master-key-2:}")
    private String masterKey2;
    
    @Value("${password.master-key-3:}")
    private String masterKey3;
    
    @Value("${password.master-key-4:}")
    private String masterKey4;
    
    /**
     * 系统启动时初始化密钥
     */
    @PostConstruct
    public void init() {
        log.info("开始初始化密码加密密钥...");
        
        // 检查是否配置了密钥
        if (StringUtils.hasText(masterKey1) && 
            StringUtils.hasText(masterKey2) &&
            StringUtils.hasText(masterKey3) && 
            StringUtils.hasText(masterKey4)) {
            
            // 从配置文件导入密钥
            String[] keys = new String[]{masterKey1, masterKey2, masterKey3, masterKey4};
            PasswordSegmentUtils.importMasterKeys(keys);
            log.info("✅ 已从配置文件加载 4 个主密钥");
            
        } else {
            // 未配置密钥，使用自动生成的临时密钥
            log.warn("⚠️  未配置密码主密钥，将使用临时生成的密钥（重启后会变化）");
            log.warn("⚠️  生产环境必须配置持久化密钥！请参考 password-keys.properties.example");
            
            // 导出自动生成的密钥到日志（仅首次）
            String[] generatedKeys = PasswordSegmentUtils.exportMasterKeys();
            log.error("========== 临时主密钥（请复制到配置文件） ==========");
            log.error("password.master-key-1={}", generatedKeys[0]);
            log.error("password.master-key-2={}", generatedKeys[1]);
            log.error("password.master-key-3={}", generatedKeys[2]);
            log.error("password.master-key-4={}", generatedKeys[3]);
            log.error("===================================================");
        }
    }
}
