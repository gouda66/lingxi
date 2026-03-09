-- =====================================================
-- 灵曦面试系统 (isi) - 完整版数据库初始化脚本
-- 版本：2.0 (含 WebRTC 实时音视频支持)
-- 作者：system
-- 日期：2026-03-09
-- =====================================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS isi_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE isi_db;

-- =====================================================
-- 1. 用户表（包含三种角色：求职者、HR、系统管理员）
-- =====================================================
CREATE TABLE IF NOT EXISTS `isi_user` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键 ID',
    `username` VARCHAR(64) NOT NULL UNIQUE COMMENT '用户名',
    `password` VARCHAR(128) NOT NULL COMMENT '密码（加密存储）',
    `real_name` VARCHAR(64) COMMENT '真实姓名',
    `email` VARCHAR(64) COMMENT '邮箱地址',
    `phone` VARCHAR(32) COMMENT '手机号码',
    `avatar_url` VARCHAR(256) COMMENT '头像 URL',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-禁用 1-正常 2-锁定',
    `role` VARCHAR(32) NOT NULL DEFAULT 'USER' COMMENT '角色：USER-求职者 HR-企业 HR ADMIN-管理员',
    `company_id` VARCHAR(64) COMMENT '所属企业 ID（仅 HR 用户）',
    `company_name` VARCHAR(128) COMMENT '所属企业名称（仅 HR 用户）',
    `department` VARCHAR(64) COMMENT '部门（仅 HR 用户）',
    `position` VARCHAR(64) COMMENT '职位（仅 HR 用户）',
    `last_login_time` DATETIME COMMENT '最后登录时间',
    `last_login_ip` VARCHAR(64) COMMENT '最后登录 IP',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    INDEX `idx_username` (`username`),
    INDEX `idx_role` (`role`),
    INDEX `idx_company_id` (`company_id`),
    INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- =====================================================
-- 2. 简历表
-- =====================================================
CREATE TABLE IF NOT EXISTS `isi_resume` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键 ID',
    `user_id` BIGINT NOT NULL COMMENT '用户 ID（求职者）',
    `title` VARCHAR(128) NOT NULL COMMENT '简历标题',
    `file_path` VARCHAR(256) NOT NULL COMMENT '简历文件存储路径',
    `file_name` VARCHAR(128) NOT NULL COMMENT '原始文件名',
    `file_type` VARCHAR(32) COMMENT '文件类型：PDF/DOC/DOCX',
    `file_size` BIGINT COMMENT '文件大小（字节）',
    `content_text` TEXT COMMENT '简历解析后的文本内容',
    `structured_data` JSON COMMENT '结构化数据 JSON',
    `skills` VARCHAR(512) COMMENT '技能标签（逗号分隔）',
    `experience_years` INT COMMENT '工作年限',
    `education` VARCHAR(32) COMMENT '最高学历',
    `major` VARCHAR(128) COMMENT '专业',
    `expected_position` VARCHAR(128) COMMENT '期望职位',
    `expected_salary` VARCHAR(64) COMMENT '期望薪资',
    `work_preference` VARCHAR(128) COMMENT '工作偏好',
    `self_evaluation` TEXT COMMENT '自我评价',
    `project_experience` JSON COMMENT '项目经历 JSON',
    `education_history` JSON COMMENT '教育经历 JSON',
    `work_experience` JSON COMMENT '工作经历 JSON',
    `status` TINYINT NOT NULL DEFAULT 0 COMMENT '状态：0-待解析 1-已解析 2-解析失败 3-面试中 4-已完成',
    `ai_score` DECIMAL(5,2) COMMENT 'AI 综合评分（百分制）',
    `match_score` DECIMAL(5,2) COMMENT '岗位匹配度得分',
    `view_count` INT DEFAULT 0 COMMENT '被查看次数',
    `download_count` INT DEFAULT 0 COMMENT '被下载次数',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_status` (`status`),
    INDEX `idx_expected_position` (`expected_position`),
    INDEX `idx_education` (`education`),
    INDEX `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='简历表';

-- =====================================================
-- 3. 面试间表（支持 WebRTC 实时音视频）
-- =====================================================
CREATE TABLE IF NOT EXISTS `isi_interview_room` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键 ID',
    `room_id` VARCHAR(64) NOT NULL UNIQUE COMMENT '房间号（唯一标识）',
    `resume_id` BIGINT NOT NULL COMMENT '简历 ID',
    `user_id` BIGINT NOT NULL COMMENT '求职者用户 ID',
    `hr_user_id` BIGINT COMMENT 'HR 用户 ID',
    `company_id` VARCHAR(64) COMMENT '企业 ID',
    `title` VARCHAR(128) NOT NULL COMMENT '面试主题',
    `description` VARCHAR(512) COMMENT '面试描述',
    `status` TINYINT NOT NULL DEFAULT 0 COMMENT '状态：0-待开始 1-进行中 2-已结束 3-已取消',
    `interview_type` TINYINT DEFAULT 1 COMMENT '面试类型：1-视频面试 2-语音面试 3-文字面试',
    `start_time` DATETIME COMMENT '预计开始时间',
    `end_time` DATETIME COMMENT '预计结束时间',
    `actual_start_time` DATETIME COMMENT '实际开始时间',
    `actual_end_time` DATETIME COMMENT '实际结束时间',
    `duration_minutes` INT COMMENT '面试时长（分钟）',
    `ai_enabled` BOOLEAN NOT NULL DEFAULT TRUE COMMENT '是否启用 AI 辅助',
    `recording_enabled` BOOLEAN DEFAULT FALSE COMMENT '是否启用录制',
    `recording_url` VARCHAR(256) COMMENT '面试录像 URL',
    `webrtc_sdp_offer` TEXT COMMENT 'WebRTC Offer SDP',
    `webrtc_sdp_answer` TEXT COMMENT 'WebRTC Answer SDP',
    `ice_candidates` JSON COMMENT 'ICE Candidates',
    `participant_count` INT DEFAULT 0 COMMENT '参与人数',
    `max_participants` INT DEFAULT 10 COMMENT '最大参与人数',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    INDEX `idx_room_id` (`room_id`),
    INDEX `idx_resume_id` (`resume_id`),
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_hr_user_id` (`hr_user_id`),
    INDEX `idx_status` (`status`),
    INDEX `idx_start_time` (`start_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='面试间表';

-- =====================================================
-- 4. AI 面试问题表
-- =====================================================
CREATE TABLE IF NOT EXISTS `isi_ai_question` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键 ID',
    `resume_id` BIGINT NOT NULL COMMENT '关联简历 ID',
    `question_type` VARCHAR(32) NOT NULL COMMENT '问题类型：TECHNICAL-技术 BEHAVIORAL-行为 CULTURAL-文化 GENERAL-通用',
    `category` VARCHAR(64) COMMENT '问题分类（如 Java、Python、沟通能力等）',
    `subcategory` VARCHAR(64) COMMENT '子分类（如 Spring、MySQL、算法等）',
    `content` TEXT NOT NULL COMMENT '问题内容',
    `difficulty` TINYINT NOT NULL DEFAULT 2 COMMENT '难度等级：1-简单 2-中等 3-困难',
    `reference_answer` TEXT COMMENT '参考答案要点',
    `evaluation_criteria` TEXT COMMENT '评分标准',
    `keywords` VARCHAR(512) COMMENT '关键词（逗号分隔）',
    `weight` DECIMAL(3,2) NOT NULL DEFAULT 1.00 COMMENT '权重（用于综合评分）',
    `ai_generated` BOOLEAN NOT NULL DEFAULT TRUE COMMENT '是否 AI 生成',
    `is_active` BOOLEAN NOT NULL DEFAULT TRUE COMMENT '是否启用',
    `usage_count` INT DEFAULT 0 COMMENT '使用次数',
    `avg_score` DECIMAL(5,2) COMMENT '平均得分',
    `created_by` BIGINT COMMENT '创建人 ID',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    INDEX `idx_resume_id` (`resume_id`),
    INDEX `idx_question_type` (`question_type`),
    INDEX `idx_category` (`category`),
    INDEX `idx_difficulty` (`difficulty`),
    INDEX `idx_ai_generated` (`ai_generated`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI 面试问题表';

-- =====================================================
-- 5. 面试问题记录表
-- =====================================================
CREATE TABLE IF NOT EXISTS `isi_interview_question_record` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键 ID',
    `room_id` BIGINT NOT NULL COMMENT '面试间 ID',
    `question_id` BIGINT COMMENT 'AI 问题 ID',
    `content` TEXT NOT NULL COMMENT '问题内容',
    `question_type` VARCHAR(32) NOT NULL COMMENT '问题类型',
    `asked_by` VARCHAR(32) NOT NULL DEFAULT 'AI' COMMENT '提问者：AI/HR/SYSTEM',
    `asked_by_user_id` BIGINT COMMENT '提问者用户 ID（如果是 HR）',
    `user_answer` TEXT COMMENT '用户回答',
    `answer_duration` INT COMMENT '回答时长（秒）',
    `ai_score` DECIMAL(5,2) COMMENT 'AI 评分（百分制）',
    `ai_feedback` TEXT COMMENT 'AI 评价反馈',
    `hr_manual_score` DECIMAL(5,2) COMMENT 'HR 手动评分',
    `hr_notes` TEXT COMMENT 'HR 备注',
    `tags` VARCHAR(256) COMMENT '标签（逗号分隔）',
    `status` TINYINT NOT NULL DEFAULT 0 COMMENT '状态：0-待回答 1-已回答 2-已跳过',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    INDEX `idx_room_id` (`room_id`),
    INDEX `idx_question_id` (`question_id`),
    INDEX `idx_asked_by` (`asked_by`),
    INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='面试问题记录表';

-- =====================================================
-- 6. 面试评估结果表（六边形雷达图）
-- =====================================================
CREATE TABLE IF NOT EXISTS `isi_interview_evaluation` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键 ID',
    `room_id` BIGINT NOT NULL UNIQUE COMMENT '面试间 ID',
    `resume_id` BIGINT NOT NULL COMMENT '简历 ID',
    `user_id` BIGINT NOT NULL COMMENT '求职者用户 ID',
    
    -- 六边形雷达图六个维度评分（0-100 分）
    `technical_skill` DECIMAL(5,2) NOT NULL DEFAULT 0.00 COMMENT '专业技能得分',
    `communication` DECIMAL(5,2) NOT NULL DEFAULT 0.00 COMMENT '沟通能力得分',
    `problem_solving` DECIMAL(5,2) NOT NULL DEFAULT 0.00 COMMENT '问题解决能力得分',
    `learning_ability` DECIMAL(5,2) NOT NULL DEFAULT 0.00 COMMENT '学习能力得分',
    `teamwork` DECIMAL(5,2) NOT NULL DEFAULT 0.00 COMMENT '团队协作能力得分',
    `cultural_fit` DECIMAL(5,2) NOT NULL DEFAULT 0.00 COMMENT '文化匹配度得分',
    
    -- 综合评估
    `total_score` DECIMAL(5,2) NOT NULL DEFAULT 0.00 COMMENT '综合总分（百分制）',
    `overall_comment` TEXT COMMENT '综合评价',
    `strengths` TEXT COMMENT '优势分析',
    `weaknesses` TEXT COMMENT '劣势分析',
    `recommendation` VARCHAR(32) COMMENT '推荐建议：STRONGLY_RECOMMEND-强烈推荐 RECOMMEND-推荐 CONSIDER-待定 NOT_RECOMMEND-不推荐',
    
    -- HR 审核信息
    `ai_generated` BOOLEAN NOT NULL DEFAULT TRUE COMMENT '是否 AI 生成',
    `hr_reviewed` BOOLEAN DEFAULT FALSE COMMENT 'HR 是否已审核',
    `hr_comments` TEXT COMMENT 'HR 评语',
    `hr_manual_adjustment` DECIMAL(5,2) COMMENT 'HR 手动调整分数',
    
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    INDEX `idx_room_id` (`room_id`),
    INDEX `idx_resume_id` (`resume_id`),
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_recommendation` (`recommendation`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='面试评估结果表';

-- =====================================================
-- 7. WebRTC 会话记录表（新增）
-- =====================================================
CREATE TABLE IF NOT EXISTS `isi_webrtc_session` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键 ID',
    `room_id` VARCHAR(64) NOT NULL COMMENT '房间 ID',
    `user_id` BIGINT NOT NULL COMMENT '用户 ID',
    `session_type` VARCHAR(32) NOT NULL DEFAULT 'VIDEO' COMMENT '会话类型：VIDEO-视频 AUDIO-语音',
    `sdp_offer` TEXT COMMENT 'Offer SDP',
    `sdp_answer` TEXT COMMENT 'Answer SDP',
    `ice_candidates_local` JSON COMMENT '本地 ICE Candidates',
    `ice_candidates_remote` JSON COMMENT '远程 ICE Candidates',
    `connection_status` VARCHAR(32) DEFAULT 'NEW' COMMENT '连接状态：NEW/CHECKING/CONNECTED/DISCONNECTED/FAILED/CLOSED',
    `quality_score` DECIMAL(5,2) COMMENT '通话质量评分',
    `bandwidth_kbps` INT COMMENT '带宽（kbps）',
    `latency_ms` INT COMMENT '延迟（毫秒）',
    `packet_loss` DECIMAL(5,2) COMMENT '丢包率',
    `start_time` DATETIME COMMENT '通话开始时间',
    `end_time` DATETIME COMMENT '通话结束时间',
    `duration_seconds` INT COMMENT '通话时长（秒）',
    `error_message` TEXT COMMENT '错误信息',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    
    INDEX `idx_room_id` (`room_id`),
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_connection_status` (`connection_status`),
    INDEX `idx_start_time` (`start_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='WebRTC 会话记录表';

-- =====================================================
-- 8. HR 操作日志表（新增）
-- =====================================================
CREATE TABLE IF NOT EXISTS `isi_hr_operation_log` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键 ID',
    `hr_user_id` BIGINT NOT NULL COMMENT 'HR 用户 ID',
    `operation_type` VARCHAR(32) NOT NULL COMMENT '操作类型：VIEW_RESUME-查看简历 ADD_QUESTION-添加问题 EDIT_QUESTION-编辑问题 DELETE_QUESTION-删除问题 START_INTERVIEW-开始面试 END_INTERVIEW-结束面试 EVALUATE-评分 DOWNLOAD-下载',
    `target_type` VARCHAR(32) COMMENT '目标类型：RESUME/QUESTION/INTERVIEW/EVALUATION',
    `target_id` BIGINT COMMENT '目标 ID',
    `operation_detail` JSON COMMENT '操作详情',
    `ip_address` VARCHAR(64) COMMENT '操作 IP',
    `user_agent` VARCHAR(256) COMMENT '用户代理',
    `result` VARCHAR(32) DEFAULT 'SUCCESS' COMMENT '结果：SUCCESS/FAILURE',
    `error_message` TEXT COMMENT '错误信息',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    
    INDEX `idx_hr_user_id` (`hr_user_id`),
    INDEX `idx_operation_type` (`operation_type`),
    INDEX `idx_target_type` (`target_type`),
    INDEX `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='HR 操作日志表';

-- =====================================================
-- 9. 管理员审计日志表（新增）
-- =====================================================
CREATE TABLE IF NOT EXISTS `isi_admin_audit_log` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键 ID',
    `admin_user_id` BIGINT NOT NULL COMMENT '管理员用户 ID',
    `audit_type` VARCHAR(32) NOT NULL COMMENT '审计类型：USER_MANAGEMENT-用户管理 RESUME_AUDIT-简历审核 INTERVIEW_MONITOR-面试监控 SYSTEM_CONFIG-系统配置 DATA_EXPORT-数据导出 PERMISSION_CHANGE-权限变更',
    `target_type` VARCHAR(32) COMMENT '目标类型',
    `target_id` BIGINT COMMENT '目标 ID',
    `before_data` JSON COMMENT '操作前数据',
    `after_data` JSON COMMENT '操作后数据',
    `audit_detail` TEXT COMMENT '审计详情',
    `ip_address` VARCHAR(64) COMMENT '操作 IP',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    
    INDEX `idx_admin_user_id` (`admin_user_id`),
    INDEX `idx_audit_type` (`audit_type`),
    INDEX `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='管理员审计日志表';

-- =====================================================
-- 10. 系统配置表（新增）
-- =====================================================
CREATE TABLE IF NOT EXISTS `isi_system_config` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键 ID',
    `config_key` VARCHAR(64) NOT NULL UNIQUE COMMENT '配置键',
    `config_value` TEXT COMMENT '配置值',
    `config_type` VARCHAR(32) DEFAULT 'STRING' COMMENT '配置类型：STRING/NUMBER/BOOLEAN/JSON',
    `description` VARCHAR(256) COMMENT '配置描述',
    `is_public` BOOLEAN DEFAULT FALSE COMMENT '是否公开',
    `update_user_id` BIGINT COMMENT '更新用户 ID',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    INDEX `idx_config_key` (`config_key`),
    INDEX `idx_config_type` (`config_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统配置表';

-- =====================================================
-- 初始化测试数据
-- =====================================================

-- 1. 管理员账号
INSERT INTO `isi_user` (`username`, `password`, `real_name`, `role`, `status`) 
VALUES ('admin', 'admin123', '系统管理员', 'ADMIN', 1);

-- 2. HR 测试账号
INSERT INTO `isi_user` (`username`, `password`, `real_name`, `role`, `company_id`, `company_name`, `department`, `position`, `status`) 
VALUES ('hr_test', 'hr123', '张 HR', 'HR', 'COMP001', '某某科技公司', '人力资源部', '招聘经理', 1);

-- 3. 求职者测试账号
INSERT INTO `isi_user` (`username`, `password`, `real_name`, `role`, `email`, `phone`, `status`) 
VALUES ('user_test', 'user123', '李小明', 'USER', 'liming@example.com', '13800138000', 1);

-- 4. 更多测试数据（可选）
-- 腾讯 HR
INSERT INTO `isi_user` (`username`, `password`, `real_name`, `role`, `company_id`, `company_name`, `status`) 
VALUES ('hr_tencent', 'hr123', '腾讯 HR', 'HR', 'COMP002', '腾讯科技', 1);

-- 阿里 HR
INSERT INTO `isi_user` (`username`, `password`, `real_name`, `role`, `company_id`, `company_name`, `status`) 
VALUES ('hr_alibaba', 'hr123', '阿里 HR', 'HR', 'COMP003', '阿里巴巴', 1);

-- 求职者 - 张三
INSERT INTO `isi_user` (`username`, `password`, `real_name`, `role`, `email`, `phone`, `status`) 
VALUES ('zhangsan', 'user123', '张三', 'USER', 'zhangsan@example.com', '13800138001', 1);

-- 求职者 - 李四
INSERT INTO `isi_user` (`username`, `password`, `real_name`, `role`, `email`, `phone`, `status`) 
VALUES ('lisi', 'user123', '李四', 'USER', 'lisi@example.com', '13800138002', 1);

-- 5. 系统配置初始数据
INSERT INTO `isi_system_config` (`config_key`, `config_value`, `config_type`, `description`, `is_public`) VALUES
('ai.enabled', 'false', 'BOOLEAN', '是否启用 AI 功能', FALSE),
('ai.api.key', 'your-api-key-here', 'STRING', 'AI API Key', FALSE),
('ai.model.name', 'gpt-4', 'STRING', 'AI 模型名称', FALSE),
('webrtc.stun.server', 'stun:stun.l.google.com:19302', 'STRING', 'STUN 服务器地址', TRUE),
('webrtc.turn.enabled', 'false', 'BOOLEAN', '是否启用 TURN 服务器', FALSE),
('interview.default.duration', '60', 'NUMBER', '默认面试时长（分钟）', TRUE),
('resume.max.file.size', '10485760', 'NUMBER', '简历最大文件大小（10MB）', TRUE),
('system.maintenance.mode', 'false', 'BOOLEAN', '系统维护模式', TRUE);

-- =====================================================
-- 视图和存储过程（可选）
-- =====================================================

-- 创建面试统计视图
CREATE OR REPLACE VIEW `v_interview_statistics` AS
SELECT 
    DATE(create_time) as interview_date,
    COUNT(*) as total_interviews,
    SUM(CASE WHEN status = 1 THEN 1 ELSE 0 END) as ongoing_interviews,
    SUM(CASE WHEN status = 2 THEN 1 ELSE 0 END) as completed_interviews,
    AVG(TIMESTAMPDIFF(MINUTE, actual_start_time, actual_end_time)) as avg_duration_minutes
FROM isi_interview_room
GROUP BY DATE(create_time);

-- 创建简历投递统计视图
CREATE OR REPLACE VIEW `v_resume_statistics` AS
SELECT 
    DATE(r.create_time) as resume_date,
    COUNT(*) as total_resumes,
    SUM(CASE WHEN r.status = 1 THEN 1 ELSE 0 END) as parsed_resumes,
    SUM(CASE WHEN r.status = 3 THEN 1 ELSE 0 END) as interviewing_resumes,
    SUM(CASE WHEN r.status = 4 THEN 1 ELSE 0 END) as completed_resumes,
    AVG(r.ai_score) as avg_ai_score
FROM isi_resume r
GROUP BY DATE(r.create_time);

-- =====================================================
-- 权限设置（可选，生产环境建议使用）
-- =====================================================

-- 创建应用用户（示例）
-- CREATE USER IF NOT EXISTS 'isi_app'@'localhost' IDENTIFIED BY 'isi_app_password';
-- GRANT SELECT, INSERT, UPDATE, DELETE ON isi_db.* TO 'isi_app'@'localhost';

-- 创建只读用户（用于报表查询）
-- CREATE USER IF NOT EXISTS 'isi_readonly'@'localhost' IDENTIFIED BY 'isi_readonly_password';
-- GRANT SELECT ON isi_db.* TO 'isi_readonly'@'localhost';

-- FLUSH PRIVILEGES;

-- =====================================================
-- 完成提示
-- =====================================================

SELECT '✅ 数据库初始化完成！' AS message;
SELECT '=====================================' AS '';
SELECT '数据库：isi_db' AS '';
SELECT '表数量：10 张' AS '';
SELECT '  - isi_user (用户表)' AS '';
SELECT '  - isi_resume (简历表)' AS '';
SELECT '  - isi_interview_room (面试间表)' AS '';
SELECT '  - isi_ai_question (AI 问题表)' AS '';
SELECT '  - isi_interview_question_record (面试记录表)' AS '';
SELECT '  - isi_interview_evaluation (评估表)' AS '';
SELECT '  - isi_webrtc_session (WebRTC 会话表)' AS '';
SELECT '  - isi_hr_operation_log (HR 操作日志表)' AS '';
SELECT '  - isi_admin_audit_log (管理员审计日志表)' AS '';
SELECT '  - isi_system_config (系统配置表)' AS '';
SELECT '=====================================' AS '';
SELECT '测试账号:' AS '';
SELECT '  管理员：admin / admin123' AS '';
SELECT '  HR: hr_test / hr123' AS '';
SELECT '  求职者：user_test / user123' AS '';
SELECT '=====================================' AS '';
