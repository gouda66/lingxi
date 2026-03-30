package com.lingxi.seo.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lingxi.seo.common.context.BaseContext;
import com.lingxi.seo.common.result.R;
import com.lingxi.seo.mapper.NginxRewriteRuleMapper;
import com.lingxi.seo.models.entity.NginxRewriteRule;
import com.lingxi.seo.service.INginxRewriteRuleService;
import com.lingxi.seo.utils.SshUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class NginxRewriteRuleServiceImpl extends ServiceImpl<NginxRewriteRuleMapper, NginxRewriteRule> implements INginxRewriteRuleService {
    
    @Autowired
    private SshUtil sshUtil;
    
    @Value("${nginx.ssh.host:}")
    private String sshHost;
    
    @Value("${nginx.ssh.port:22}")
    private int sshPort;
    
    @Value("${nginx.ssh.username:}")
    private String sshUsername;
    
    @Value("${nginx.ssh.password:}")
    private String sshPassword;
    
    @Value("${nginx.config.path:/etc/nginx/conf.d}")
    private String nginxConfigPath;
    
    @Override
    public R listPage(Map<String, Object> params) {
        Integer pageNum = Integer.parseInt(params.getOrDefault("pageNum", "1").toString());
        Integer pageSize = Integer.parseInt(params.getOrDefault("pageSize", "10").toString());
        String ruleName = (String) params.get("ruleName");
        String category = (String) params.get("category");
        
        Page<NginxRewriteRule> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<NginxRewriteRule> wrapper = new LambdaQueryWrapper<>();
        
        if (StrUtil.isNotBlank(ruleName)) {
            wrapper.like(NginxRewriteRule::getRuleName, ruleName);
        }
        
        if (StrUtil.isNotBlank(category)) {
            wrapper.eq(NginxRewriteRule::getCategory, category);
        }
        
        wrapper.orderByDesc(NginxRewriteRule::getSortOrder);
        
        Page<NginxRewriteRule> resultPage = this.page(page, wrapper);
        
        return R.success(resultPage);
    }
    
    @Override
    public R getDetail(Long id) {
        NginxRewriteRule rule = this.getById(id);
        if (rule == null) {
            return R.error("规则不存在");
        }
        return R.success(rule);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public R addRule(Map<String, Object> data) {
        try {
            NginxRewriteRule rule = new NginxRewriteRule();
            rule.setRuleName((String) data.get("ruleName"));
            rule.setOriginalPattern((String) data.get("originalPattern"));
            rule.setReplacementUrl((String) data.get("replacementUrl"));
            rule.setFlag((String) data.getOrDefault("flag", "last"));
            rule.setDescription((String) data.get("description"));
            rule.setSortOrder(Integer.parseInt(data.getOrDefault("sortOrder", "0").toString()));
            rule.setIsEnabled(Integer.parseInt(data.getOrDefault("isEnabled", "1").toString()));
            rule.setCategory((String) data.getOrDefault("category", "GENERAL"));
            
            this.save(rule);
            return R.success("添加成功");
        } catch (Exception e) {
            log.error("添加重写规则失败", e);
            return R.error("添加失败：" + e.getMessage());
        }
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public R updateRule(Map<String, Object> data) {
        try {
            Long id = Long.parseLong(data.get("id").toString());
            NginxRewriteRule rule = this.getById(id);
            if (rule == null) {
                return R.error("规则不存在");
            }
            
            rule.setRuleName((String) data.get("ruleName"));
            rule.setOriginalPattern((String) data.get("originalPattern"));
            rule.setReplacementUrl((String) data.get("replacementUrl"));
            rule.setFlag((String) data.getOrDefault("flag", "last"));
            rule.setDescription((String) data.get("description"));
            rule.setSortOrder(Integer.parseInt(data.getOrDefault("sortOrder", "0").toString()));
            rule.setIsEnabled(Integer.parseInt(data.getOrDefault("isEnabled", "1").toString()));
            rule.setCategory((String) data.getOrDefault("category", "GENERAL"));
            
            this.updateById(rule);
            return R.success("更新成功");
        } catch (Exception e) {
            log.error("更新重写规则失败", e);
            return R.error("更新失败：" + e.getMessage());
        }
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public R deleteRule(String ids) {
        try {
            List<Long> idList = StrUtil.split(ids, ',').stream()
                    .map(Long::parseLong)
                    .toList();
            this.removeByIds(idList);
            return R.success("删除成功");
        } catch (Exception e) {
            log.error("删除重写规则失败", e);
            return R.error("删除失败：" + e.getMessage());
        }
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public R syncToNginx() {
        try {
            // 1. 检查 SSH 配置
            if (StrUtil.isBlank(sshHost) || StrUtil.isBlank(sshUsername) || StrUtil.isBlank(sshPassword)) {
                return R.error("SSH 配置未设置，请在 application.yml 中配置 nginx.ssh.* 相关参数");
            }

            // 2. 测试 SSH 连接
            log.info("测试 SSH 连接：{}@{}:{}", sshUsername, sshHost, sshPort);
            boolean connected = sshUtil.testConnection(sshHost, sshPort, sshUsername, sshPassword);
            if (!connected) {
                return R.error("SSH 连接失败，请检查服务器地址、端口、用户名或密码");
            }

            // 3. 查询所有启用的重写规则
            LambdaQueryWrapper<NginxRewriteRule> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(NginxRewriteRule::getIsEnabled, 1)
                    .orderByDesc(NginxRewriteRule::getSortOrder);

            List<NginxRewriteRule> rules = this.list(wrapper);

            if (rules.isEmpty()) {
                log.warn("没有找到启用的重写规则");
            }

            // 4. 生成重写规则内容
            StringBuilder rewriteRules = new StringBuilder();
            rewriteRules.append("\n        # Nginx 重写规则（由 SEO 管理系统自动生成）\n");
            rewriteRules.append("        # 生成时间：").append(LocalDateTime.now()).append("\n");

            for (NginxRewriteRule rule : rules) {
                rewriteRules.append("        # ").append(rule.getRuleName());
                if (StrUtil.isNotBlank(rule.getDescription())) {
                    rewriteRules.append(" - ").append(rule.getDescription());
                }
                rewriteRules.append("\n");

                // 根据 flag 判断生成 rewrite 还是 return 301
                String flag = rule.getFlag();
                String rewriteDirective;

                if ("redirect".equals(flag) || "permanent".equals(flag)) {
                    // 生成 301/302 重定向（用于 SEO 优化和外链跳转）
                    rewriteDirective = String.format("        if ( $request_uri ~* \"%s\" ) {\n            return 301 %s;\n        }\n",
                            rule.getOriginalPattern(),
                            rule.getReplacementUrl());
                } else {
                    // 生成普通 rewrite（内部重写，用于伪静态）
                    rewriteDirective = String.format("        rewrite %s %s %s;\n",
                            rule.getOriginalPattern(),
                            rule.getReplacementUrl(),
                            flag);
                }

                rewriteRules.append(rewriteDirective).append("\n");
            }

            // 5. 读取原始配置文件
            String configFilePath = "/etc/nginx/conf.d/seo-web.conf";
            log.info("读取配置文件：{}", configFilePath);
            String originalContent = sshUtil.readFile(sshHost, sshPort, sshUsername, sshPassword, configFilePath);

            if (originalContent == null || originalContent.isEmpty()) {
                return R.error("读取配置文件失败");
            }

            // 6. 在 location / 块中插入重写规则
            String updatedContent = insertRewriteRules(originalContent, rewriteRules.toString());

            // 7. 写回配置文件
            log.info("写入配置文件...");
            boolean written = sshUtil.writeFile(sshHost, sshPort, sshUsername, sshPassword, configFilePath, updatedContent);

            if (!written) {
                return R.error("写入配置文件失败");
            }

            // 8. 测试 Nginx 配置
            log.info("测试 Nginx 配置...");
            String testResult = sshUtil.executeCommand(sshHost, sshPort, sshUsername, sshPassword,
                    "nginx -t");

            if (testResult.contains("syntax is ok") && testResult.contains("test is successful")) {
                log.info("Nginx 配置测试通过");
            } else {
                log.error("Nginx 配置测试失败：{}", testResult);
                // 恢复原始配置
                sshUtil.writeFile(sshHost, sshPort, sshUsername, sshPassword, configFilePath, originalContent);
                return R.error("Nginx 配置测试失败：" + testResult);
            }

            // 9. 重新加载 Nginx 配置
            log.info("重新加载 Nginx 配置...");
            String reloadResult = sshUtil.executeCommand(sshHost, sshPort, sshUsername, sshPassword,
                    "nginx -s reload");

            // 检查 reload 是否成功
            if (reloadResult.contains("signal process started")) {
                log.info("Nginx 配置重新加载成功");
            } else {
                log.warn("Nginx 重新加载结果：{}", reloadResult);
            }

            log.info("Nginx 配置同步成功，重新加载结果：{}", reloadResult);

            return R.success("配置已成功同步到 Nginx 服务器并重新加载");


        } catch (Exception e) {
            log.error("同步 Nginx 配置失败", e);
            return R.error("同步失败：" + e.getMessage());
        }
    }

    /**
     * 在 location / 块中插入重写规则（覆盖旧规则）
     */
    private String insertRewriteRules(String originalContent, String rewriteRules) {
        // 查找 location / 块
        int locationStart = originalContent.indexOf("location / {");
        if (locationStart == -1) {
            throw new RuntimeException("未找到 location / 块");
        }

        // 查找 try_files 语句
        int tryFilesStart = originalContent.indexOf("try_files $uri $uri/ /index.html;", locationStart);
        if (tryFilesStart == -1) {
            throw new RuntimeException("未找到 try_files 语句");
        }

        // 查找 try_files 语句的结束位置
        int tryFilesEnd = tryFilesStart + "try_files $uri $uri/ /index.html;".length();

        // 查找是否已有旧的规则注释（如果有，删除旧规则）
        int oldRuleStart = originalContent.indexOf("# Nginx 重写规则（由 SEO 管理系统自动生成）", locationStart);

        // 查找 location / 块的结束位置（下一个 location 或 server 块结束）
        int nextLocation = originalContent.indexOf("location", tryFilesEnd);
        int nextCloseBrace = originalContent.indexOf("}", tryFilesEnd);

        int locationEnd;
        if (nextLocation != -1 && (nextCloseBrace == -1 || nextLocation < nextCloseBrace)) {
            locationEnd = nextLocation;
        } else if (nextCloseBrace != -1) {
            locationEnd = nextCloseBrace;
        } else {
            locationEnd = originalContent.length();
        }

        String updatedContent;

        if (oldRuleStart != -1 && oldRuleStart < locationEnd) {
            // 找到旧规则的结束位置（下一个 location 或 } 之前）
            int oldRuleEnd = originalContent.indexOf("#", oldRuleStart + 10);
            if (oldRuleEnd == -1 || oldRuleEnd > locationEnd) {
                oldRuleEnd = locationEnd;
            }

            // 跳过换行符
            while (oldRuleEnd < originalContent.length() &&
                    (originalContent.charAt(oldRuleEnd) == '\n' || originalContent.charAt(oldRuleEnd) == '\r')) {
                oldRuleEnd++;
            }

            // 删除旧规则，插入新规则
            updatedContent = originalContent.substring(0, tryFilesEnd) + "\n" + rewriteRules + "\n" + originalContent.substring(oldRuleEnd);
        } else {
            // 没有旧规则，直接在 try_files 后面插入
            updatedContent = originalContent.substring(0, tryFilesEnd) + "\n" + rewriteRules + "\n" + originalContent.substring(tryFilesEnd);
        }

        return updatedContent;
    }

}
