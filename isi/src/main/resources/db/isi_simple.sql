
-- AI智能面试系统数据库设计（极度简化版 - 用于生成E-R图）
-- 基于Spring AI + MCP架构
-- MySQL 8.0+ 语法

-- ============================================
-- 1. 简历管理模块
-- ============================================

-- 简历主表
CREATE TABLE `resume` (
                          `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
                          `user_id` BIGINT UNSIGNED NOT NULL,
                          `resume_name` VARCHAR(128) NOT NULL,
                          `candidate_name` VARCHAR(64) NOT NULL,
                          `contact_info` VARCHAR(255),
                          PRIMARY KEY (`id`)
);

-- 简历技能表
CREATE TABLE `resume_skill` (
                                `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
                                `resume_id` BIGINT UNSIGNED NOT NULL,
                                `skill_name` VARCHAR(64) NOT NULL,
                                `proficiency` TINYINT,
                                PRIMARY KEY (`id`)
);

-- 简历项目经验表
CREATE TABLE `resume_project` (
                                  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
                                  `resume_id` BIGINT UNSIGNED NOT NULL,
                                  `project_name` VARCHAR(128) NOT NULL,
                                  `role` VARCHAR(64),
                                  `description` TEXT,
                                  PRIMARY KEY (`id`)
);

-- ============================================
-- 2. 面试会话模块
-- ============================================

-- 面试会话表
CREATE TABLE `interview_session` (
                                     `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
                                     `session_code` VARCHAR(32) NOT NULL,
                                     `candidate_id` BIGINT UNSIGNED NOT NULL,
                                     `resume_id` BIGINT UNSIGNED NOT NULL,
                                     `status` VARCHAR(20) NOT NULL DEFAULT 'CREATED',
                                     PRIMARY KEY (`id`)
);

-- ============================================
-- 3. 面试内容模块
-- ============================================

-- 面试题目表
CREATE TABLE `interview_question` (
                                      `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
                                      `session_id` BIGINT UNSIGNED NOT NULL,
                                      `question_content` TEXT NOT NULL,
                                      `difficulty` TINYINT DEFAULT 2,
                                      `sequence_no` INT NOT NULL,
                                      PRIMARY KEY (`id`)
);

-- 面试回答表
CREATE TABLE `interview_answer` (
                                    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
                                    `question_id` BIGINT UNSIGNED NOT NULL,
                                    `session_id` BIGINT UNSIGNED NOT NULL,
                                    `answer_content` TEXT,
                                    `ai_score` DECIMAL(3,1),
                                    PRIMARY KEY (`id`)
);

-- ============================================
-- 4. 面试报告模块
-- ============================================

-- 面试综合报告表
CREATE TABLE `interview_report` (
                                    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
                                    `session_id` BIGINT UNSIGNED NOT NULL,
                                    `candidate_id` BIGINT UNSIGNED NOT NULL,
                                    `total_score` DECIMAL(4,2) NOT NULL,
                                    `recommendation` TINYINT DEFAULT 0,
                                    PRIMARY KEY (`id`)
);