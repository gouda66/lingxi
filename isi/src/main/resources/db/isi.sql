-- 灵曦面试系统 (isi) 数据库初始化脚本

CREATE DATABASE IF NOT EXISTS isi_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE isi_db;

-- 1. 用户表（包含三种角色：求职者、HR、管理员）
CREATE TABLE IF NOT EXISTS `isi_user` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键 ID',
    `username` VARCHAR(64) NOT NULL UNIQUE COMMENT '用户名',
    `password` VARCHAR(128) NOT NULL COMMENT '密码（加密）',
    `real_name` VARCHAR(64) COMMENT '真实姓名',
    `email` VARCHAR(64) COMMENT '邮箱',
    `phone` VARCHAR(32) COMMENT '手机号',
    `status` INT NOT NULL DEFAULT 1 COMMENT '状态 0 禁用 1 正常',
    `role` VARCHAR(32) NOT NULL DEFAULT 'USER' COMMENT '角色 USER-求职者 HR-企业 HR ADMIN-管理员',
    `company_id` VARCHAR(64) COMMENT '所属企业 ID（仅 HR 用户有值）',
    `company_name` VARCHAR(128) COMMENT '所属企业名称（仅 HR 用户有值）',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_username` (`username`),
    INDEX `idx_role` (`role`),
    INDEX `idx_company_id` (`company_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='求职者用户表';

-- 2. 简历表
CREATE TABLE IF NOT EXISTS `isi_resume` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键 ID',
    `user_id` BIGINT NOT NULL COMMENT '用户 ID（求职者）',
    `title` VARCHAR(128) NOT NULL COMMENT '简历标题',
    `file_path` VARCHAR(256) NOT NULL COMMENT '简历文件存储路径',
    `file_name` VARCHAR(128) NOT NULL COMMENT '原始文件名',
    `file_type` VARCHAR(32) COMMENT '文件类型 PDF/DOC/DOCX',
    `content_text` TEXT COMMENT '简历解析后的文本内容',
    `structured_data` JSON COMMENT '结构化数据 JSON（个人信息、教育经历、工作经历等）',
    `skills` VARCHAR(512) COMMENT '技能标签（逗号分隔）',
    `experience_years` INT COMMENT '工作年限',
    `education` VARCHAR(32) COMMENT '最高学历',
    `major` VARCHAR(128) COMMENT '专业',
    `expected_position` VARCHAR(128) COMMENT '期望职位',
    `expected_salary` VARCHAR(64) COMMENT '期望薪资',
    `status` INT NOT NULL DEFAULT 0 COMMENT '状态 0-待处理 1-已解析 2-解析失败 3-面试中 4-已完成',
    `ai_score` DOUBLE COMMENT 'AI 综合评分（百分制）',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_status` (`status`),
    INDEX `idx_expected_position` (`expected_position`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='简历表';

-- 3. 面试间表
CREATE TABLE IF NOT EXISTS `isi_interview_room` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键 ID',
    `room_id` VARCHAR(64) NOT NULL UNIQUE COMMENT '房间号（唯一标识）',
    `resume_id` BIGINT NOT NULL COMMENT '简历 ID',
    `user_id` BIGINT NOT NULL COMMENT '求职者用户 ID',
    `hr_user_id` BIGINT COMMENT 'HR 用户 ID（可为空，支持多 HR）',
    `company_id` VARCHAR(64) COMMENT '企业 ID',
    `title` VARCHAR(128) NOT NULL COMMENT '面试主题',
    `status` INT NOT NULL DEFAULT 0 COMMENT '状态 0-待开始 1-进行中 2-已结束 3-已取消',
    `start_time` DATETIME COMMENT '预计开始时间',
    `end_time` DATETIME COMMENT '预计结束时间',
    `actual_start_time` DATETIME COMMENT '实际开始时间',
    `actual_end_time` DATETIME COMMENT '实际结束时间',
    `ai_enabled` BOOLEAN NOT NULL DEFAULT TRUE COMMENT '是否启用 AI 辅助',
    `recording_url` VARCHAR(256) COMMENT '面试录像 URL',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_room_id` (`room_id`),
    INDEX `idx_resume_id` (`resume_id`),
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_hr_user_id` (`hr_user_id`),
    INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='面试间表';

-- 4. AI 面试问题表
CREATE TABLE IF NOT EXISTS `isi_ai_question` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键 ID',
    `resume_id` BIGINT NOT NULL COMMENT '关联简历 ID',
    `question_type` VARCHAR(32) NOT NULL COMMENT '问题类型 TECHNICAL-技术 BEHAVIORAL-行为 CULTURAL-文化匹配 GENERAL-通用',
    `category` VARCHAR(64) COMMENT '问题分类（如 Java、Python、沟通能力等）',
    `content` TEXT NOT NULL COMMENT '问题内容',
    `difficulty` INT NOT NULL DEFAULT 2 COMMENT '难度等级 1-简单 2-中等 3-困难',
    `reference_answer` TEXT COMMENT '参考答案要点',
    `evaluation_criteria` TEXT COMMENT '评分标准',
    `weight` DOUBLE NOT NULL DEFAULT 1.0 COMMENT '权重（用于综合评分）',
    `ai_generated` BOOLEAN NOT NULL DEFAULT TRUE COMMENT '是否 AI 生成',
    `status` INT NOT NULL DEFAULT 1 COMMENT '状态 0-草稿 1-启用 2-禁用',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_resume_id` (`resume_id`),
    INDEX `idx_question_type` (`question_type`),
    INDEX `idx_category` (`category`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI 面试问题表';

-- 5. 面试问题记录表
CREATE TABLE IF NOT EXISTS `isi_interview_question_record` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键 ID',
    `room_id` BIGINT NOT NULL COMMENT '面试间 ID',
    `question_id` BIGINT COMMENT 'AI 问题 ID（如果是 AI 题库中的问题）',
    `content` TEXT NOT NULL COMMENT '问题内容',
    `question_type` VARCHAR(32) NOT NULL COMMENT '问题类型',
    `asked_by` VARCHAR(32) NOT NULL DEFAULT 'AI' COMMENT '提问者 AI/HR/SYSTEM',
    `user_answer` TEXT COMMENT '用户回答',
    `answer_duration` INT COMMENT '回答时长（秒）',
    `ai_score` DOUBLE COMMENT 'AI 评分（百分制）',
    `ai_feedback` TEXT COMMENT 'AI 评价反馈',
    `hr_manual_score` DOUBLE COMMENT 'HR 手动评分（可选）',
    `hr_notes` TEXT COMMENT 'HR 备注',
    `status` INT NOT NULL DEFAULT 0 COMMENT '状态 0-待回答 1-已回答 2-已跳过',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_room_id` (`room_id`),
    INDEX `idx_question_id` (`question_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='面试问题记录表';

-- 6. 面试评估结果表（包含六边形雷达图数据）
CREATE TABLE IF NOT EXISTS `isi_interview_evaluation` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键 ID',
    `room_id` BIGINT NOT NULL UNIQUE COMMENT '面试间 ID',
    `resume_id` BIGINT NOT NULL COMMENT '简历 ID',
    `user_id` BIGINT NOT NULL COMMENT '求职者用户 ID',
    `technical_skill` DOUBLE NOT NULL DEFAULT 0.0 COMMENT '专业技能得分',
    `communication` DOUBLE NOT NULL DEFAULT 0.0 COMMENT '沟通能力得分',
    `problem_solving` DOUBLE NOT NULL DEFAULT 0.0 COMMENT '问题解决能力得分',
    `learning_ability` DOUBLE NOT NULL DEFAULT 0.0 COMMENT '学习能力得分',
    `teamwork` DOUBLE NOT NULL DEFAULT 0.0 COMMENT '团队协作能力得分',
    `cultural_fit` DOUBLE NOT NULL DEFAULT 0.0 COMMENT '文化匹配度得分',
    `total_score` DOUBLE NOT NULL DEFAULT 0.0 COMMENT '综合总分（百分制）',
    `overall_comment` TEXT COMMENT '综合评价',
    `strengths` TEXT COMMENT '优势分析',
    `weaknesses` TEXT COMMENT '劣势分析',
    `recommendation` VARCHAR(32) COMMENT '推荐建议 STRONGLY_RECOMMEND-强烈推荐 RECOMMEND-推荐 CONSIDER-待定 NOT_RECOMMEND-不推荐',
    `ai_generated` BOOLEAN NOT NULL DEFAULT TRUE COMMENT '是否 AI 生成',
    `hr_reviewed` BOOLEAN DEFAULT FALSE COMMENT 'HR 是否已审核',
    `hr_comments` TEXT COMMENT 'HR 评语',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_room_id` (`room_id`),
    INDEX `idx_resume_id` (`resume_id`),
    INDEX `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='面试评估结果表';

-- 插入测试数据
-- 1. 管理员账号
INSERT INTO `isi_user` (`username`, `password`, `real_name`, `role`, `status`) 
VALUES ('admin', 'admin123', '系统管理员', 'ADMIN', 1);

-- 2. HR 测试账号
INSERT INTO `isi_user` (`username`, `password`, `real_name`, `role`, `company_id`, `company_name`, `status`) 
VALUES ('hr_test', 'hr123', '张 HR', 'HR', 'COMP001', '某某科技公司', 1);

-- 3. 求职者测试账号
INSERT INTO `isi_user` (`username`, `password`, `real_name`, `role`, `status`) 
VALUES ('user_test', 'user123', '李小明', 'USER', 1);

-- 4. 更多测试数据（可选）
-- 企业 HR 账号
INSERT INTO `isi_user` (`username`, `password`, `real_name`, `role`, `company_id`, `company_name`, `status`) 
VALUES ('hr_tencent', 'hr123', '腾讯 HR', 'HR', 'COMP002', '腾讯科技', 1);

INSERT INTO `isi_user` (`username`, `password`, `real_name`, `role`, `company_id`, `company_name`, `status`) 
VALUES ('hr_alibaba', 'hr123', '阿里 HR', 'HR', 'COMP003', '阿里巴巴', 1);

-- 更多求职者账号
INSERT INTO `isi_user` (`username`, `password`, `real_name`, `role`, `email`, `phone`, `status`) 
VALUES ('zhangsan', 'user123', '张三', 'USER', 'zhangsan@example.com', '13800138001', 1);

INSERT INTO `isi_user` (`username`, `password`, `real_name`, `role`, `email`, `phone`, `status`) 
VALUES ('lisi', 'user123', '李四', 'USER', 'lisi@example.com', '13800138002', 1);
