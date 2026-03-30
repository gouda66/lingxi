-- Nginx 重写规则与 SEO 优化管理系统数据库脚本
-- 创建时间：2026-03-28
-- MySQL 8.0+ 语法

-- ============================================
-- 1. Nginx 重写规则配置表
-- ============================================

CREATE TABLE IF NOT EXISTS `nginx_rewrite_rule` (
                                                    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键 ID',
    `rule_name` varchar(100) NOT NULL COMMENT '规则名称',
    `original_pattern` varchar(500) NOT NULL COMMENT '原始 URL 模式',
    `replacement_url` varchar(500) NOT NULL COMMENT '替换后的 URL',
    `flag` varchar(50) DEFAULT 'last' COMMENT 'Nginx 标志 (last/break/redirect/permanent)',
    `description` varchar(500) DEFAULT NULL COMMENT '规则描述',
    `sort_order` int(11) DEFAULT '0' COMMENT '排序顺序',
    `is_enabled` tinyint(1) DEFAULT '1' COMMENT '是否启用 (0-禁用 1-启用)',
    `category` varchar(50) DEFAULT 'GENERAL' COMMENT '分类 (GENERAL/SEO/API)',
    `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_category` (`category`),
    KEY `idx_enabled` (`is_enabled`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Nginx 重写规则配置表';

-- ============================================
-- 2. SEO 配置表
-- ============================================

CREATE TABLE IF NOT EXISTS `seo_config` (
                                            `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键 ID',
    `path` varchar(500) NOT NULL COMMENT 'URL 路径',
    `title` varchar(200) DEFAULT NULL COMMENT '页面标题',
    `keywords` varchar(500) DEFAULT NULL COMMENT '关键词',
    `description` varchar(1000) DEFAULT NULL COMMENT '描述',
    `canonical_url` varchar(500) DEFAULT NULL COMMENT '规范 URL',
    `robots` varchar(50) DEFAULT 'index,follow' COMMENT 'Robots 指令',
    `og_image` varchar(500) DEFAULT NULL COMMENT 'OpenGraph 图片',
    `is_enabled` tinyint(1) DEFAULT '1' COMMENT '是否启用',
    `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_path` (`path`(255)),
    KEY `idx_enabled` (`is_enabled`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='SEO 配置表';

-- ============================================
-- 3. Nginx 配置历史表
-- ============================================

CREATE TABLE IF NOT EXISTS `nginx_config_history` (
                                                      `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键 ID',
    `config_content` text NOT NULL COMMENT '配置文件内容',
    `version` varchar(50) NOT NULL COMMENT '版本号',
    `description` varchar(500) DEFAULT NULL COMMENT '版本描述',
    `operator_id` bigint(20) DEFAULT NULL COMMENT '操作人 ID',
    `operator_name` varchar(100) DEFAULT NULL COMMENT '操作人姓名',
    `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_version` (`version`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Nginx 配置历史表';

-- ============================================
-- 4. Nginx 请求日志表
-- ============================================

CREATE TABLE IF NOT EXISTS `nginx_request_log` (
                                                   `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键 ID',
                                                   `request_name` varchar(100) DEFAULT NULL COMMENT '请求名称',
    `request_path` varchar(500) NOT NULL COMMENT '请求路径',
    `method` varchar(10) DEFAULT NULL COMMENT '请求方法',
    `original_url` varchar(1000) DEFAULT NULL COMMENT '原始 URL',
    `rewritten_url` varchar(1000) DEFAULT NULL COMMENT '重写后的 URL',
    `rule_name` varchar(100) DEFAULT NULL COMMENT '匹配的规则名称',
    `rule_flag` varchar(20) DEFAULT NULL COMMENT '规则标志 (last/redirect/permanent)',
    `status_code` int DEFAULT NULL COMMENT '响应状态码',
    `response_time` bigint DEFAULT NULL COMMENT '响应时间 (ms)',
    `user_agent` varchar(500) DEFAULT NULL COMMENT '用户代理',
    `remote_addr` varchar(50) DEFAULT NULL COMMENT '客户端 IP',
    `referer` varchar(1000) DEFAULT NULL COMMENT '来源页面',
    `is_rewritten` tinyint DEFAULT 0 COMMENT '是否被重写 (0:否 1:是)',
    `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_request_path` (`request_path`),
    KEY `idx_created_at` (`created_at`),
    KEY `idx_rule_name` (`rule_name`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Nginx 请求日志表';



-- ============================================
-- 5. 系统用户表
-- =================


CREATE TABLE `sys_user` (
                            `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                            `username` VARCHAR(64) NOT NULL COMMENT '用户名',
                            `email` VARCHAR(128) NULL COMMENT '邮箱（唯一）',
                            `password` VARCHAR(128) NOT NULL COMMENT '加密密码',
                            `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
                            `real_name` VARCHAR(64) DEFAULT NULL COMMENT '真实姓名',
                            `avatar_url` VARCHAR(255) DEFAULT NULL COMMENT '头像URL',
                            `role` TINYINT NOT NULL DEFAULT 1 COMMENT '角色：1-面试者 2-HR 3-管理员',
                            `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-禁用 1-正常',
                            `last_login_time` DATETIME DEFAULT NULL COMMENT '最后登录时间',
                            `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                            `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                            `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除',
                            PRIMARY KEY (`id`),
                            UNIQUE KEY `uk_email` (`email`),
                            UNIQUE KEY `uk_username` (`username`),
                            KEY `idx_role_status` (`role`, `status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';
