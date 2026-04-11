-- AI智能面试系统数据库设计
-- 基于Spring AI + MCP架构
-- MySQL 8.0+ 语法

-- ============================================
-- 1. 用户与权限模块
-- ============================================

-- 用户基础表（面试者、HR、管理员）
CREATE TABLE `sys_user` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_name` VARCHAR(64) NOT NULL COMMENT '用户名',
    `email` VARCHAR(128) NULL COMMENT '邮箱（唯一）',
    `password` VARCHAR(128) NOT NULL COMMENT '加密密码',
    `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
    `real_name` VARCHAR(64) DEFAULT NULL COMMENT '真实姓名',
    `avatar_url` VARCHAR(255) DEFAULT NULL COMMENT '头像URL',
    `role` INT NOT NULL DEFAULT 1 COMMENT '角色：1-面试者 2-HR 3-管理员',
    `status` TINYINT NOT NULL DEFAULT 0 COMMENT '状态：0-正常 1-禁用',
    `last_login_time` DATETIME DEFAULT NULL COMMENT '最后登录时间',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除',
    `sex` TINYINT DEFAULT NULL COMMENT '性别：0-女 1-男 2-未知',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_email` (`email`),
    UNIQUE KEY `uk_user_name` (`user_name`),
    KEY `idx_role_status` (`role`, `status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 系统密钥配置表
CREATE TABLE `system_secret_key` (
    `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
    `key_name` VARCHAR(64) NOT NULL COMMENT '密钥名称',
    `key_value` TEXT NOT NULL COMMENT 'Base64 编码的密钥值',
    `algorithm` VARCHAR(32) DEFAULT 'AES' COMMENT '加密算法',
    `is_active` TINYINT DEFAULT 1 COMMENT '是否激活',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_key_name` (`key_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统密钥配置表';

-- 插入初始密钥（启动时会自动生成并更新）
INSERT INTO `system_secret_key` (`key_name`, `key_value`, `algorithm`, `is_active`) VALUES
('MASTER_KEY_0', '', 'AES', 1);

-- 菜单权限表
CREATE TABLE `sys_menu` (
    `menu_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `menu_name` VARCHAR(64) NOT NULL COMMENT '菜单名称',
    `parent_id` BIGINT UNSIGNED DEFAULT 0 COMMENT '父菜单 ID',
    `order_num` INT DEFAULT 0 COMMENT '显示顺序',
    `path` VARCHAR(200) DEFAULT '' COMMENT '路由地址',
    `component` VARCHAR(255) DEFAULT NULL COMMENT '组件路径',
    `query` VARCHAR(255) DEFAULT NULL COMMENT '路由参数',
    `is_frame` TINYINT DEFAULT 1 COMMENT '是否为外链',
    `is_cache` TINYINT DEFAULT 0 COMMENT '是否缓存',
    `menu_type` CHAR(1) DEFAULT '' COMMENT '菜单类型（M 目录 C 菜单 F 按钮）',
    `visible` TINYINT DEFAULT 1 COMMENT '菜单状态（1 显示 0 隐藏）',
    `status` CHAR(1) DEFAULT '0' COMMENT '菜单状态（0 正常 1 停用）',
    `perms` VARCHAR(100) DEFAULT NULL COMMENT '权限标识',
    `icon` VARCHAR(100) DEFAULT '#' COMMENT '菜单图标',
    `remark` VARCHAR(500) DEFAULT '' COMMENT '备注',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`menu_id`),
    KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='菜单权限表';

-- 角色菜单关联表
CREATE TABLE `sys_role_menu` (
    `role_id` BIGINT UNSIGNED NOT NULL COMMENT '角色 ID',
    `menu_id` BIGINT UNSIGNED NOT NULL COMMENT '菜单 ID',
    PRIMARY KEY (`role_id`, `menu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色菜单关联表';

-- 初始化菜单数据
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `menu_type`, `visible`, `status`, `perms`, `icon`) VALUES
(1, '系统管理', 0, 1, 'system', 'Layout', 'M', 1, '0', 'system:menu', 'monitor'),
(2, '用户管理', 1, 1, 'user', 'system/user/index', 'C', 1, '0', 'system:user:list', 'user'),
(3, '角色管理', 1, 2, 'role', 'system/role/index', 'C', 1, '0', 'system:role:list', 'peoples'),
(4, '菜单管理', 1, 3, 'menu', 'system/menu/index', 'C', 1, '0', 'system:menu:list', 'tree-table'),
(5, '面试管理', 0, 2, 'interview', 'Layout', 'M', 1, '0', 'interview:menu', 'skill'),
(6, '面试会话', 5, 1, 'session', 'interview/session/index', 'C', 1, '0', 'interview:session:list', 'eye-open'),
(7, '简历管理', 5, 2, 'resume', 'interview/resume/index', 'C', 1, '0', 'interview:resume:list', 'star');

-- 系统通知公告表
CREATE TABLE `sys_notice` (
    `notice_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `notice_title` VARCHAR(100) NOT NULL COMMENT '公告标题',
    `notice_type` CHAR(1) NOT NULL COMMENT '公告类型（1 通知 2 公告）',
    `notice_content` TEXT COMMENT '公告内容',
    `status` CHAR(1) DEFAULT '0' COMMENT '状态（0 正常 1 停用）',
    `creator` VARCHAR(64) DEFAULT '' COMMENT '创建者',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `remark` VARCHAR(255) DEFAULT '' COMMENT '备注',
    PRIMARY KEY (`notice_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='通知公告表';

-- 插入测试数据
INSERT INTO `sys_notice` (`notice_title`, `notice_type`, `notice_content`, `status`, `creator`) VALUES
('欢迎使用 AI 智能面试系统', '1', '<p>欢迎使用 AI 智能面试系统，祝您使用愉快！</p>', '0', 'admin');

-- ============================================
-- 2. 简历管理模块
-- ============================================

-- 简历主表
CREATE TABLE `resume` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `user_id` BIGINT UNSIGNED NOT NULL COMMENT '所属用户ID',
    `resume_name` VARCHAR(128) NOT NULL COMMENT '简历名称（如：Java开发工程师-张三）',
    `original_file_url` VARCHAR(255) NOT NULL COMMENT '原始文件URL（PDF/DOCX）',
    `file_type` VARCHAR(10) NOT NULL COMMENT '文件类型：pdf/docx/txt',
    `file_size` INT UNSIGNED DEFAULT NULL COMMENT '文件大小（字节）',
    `parse_status` TINYINT NOT NULL DEFAULT 0 COMMENT '解析状态：0-待解析 1-解析中 2-解析成功 3-解析失败',
    `parse_error_msg` TEXT DEFAULT NULL COMMENT '解析错误信息',
    `full_name` VARCHAR(64) DEFAULT NULL COMMENT '姓名',
    `gender` TINYINT DEFAULT NULL COMMENT '性别：0-女 1-男',
    `birth_date` DATE DEFAULT NULL COMMENT '出生日期',
    `phone` VARCHAR(20) DEFAULT NULL COMMENT '电话',
    `email` VARCHAR(128) DEFAULT NULL COMMENT '邮箱',
    `location` VARCHAR(128) DEFAULT NULL COMMENT '所在地',
    `job_title` VARCHAR(128) DEFAULT NULL COMMENT '求职意向/目标职位',
    `expected_salary` VARCHAR(64) DEFAULT NULL COMMENT '期望薪资',
    `self_evaluation` TEXT DEFAULT NULL COMMENT '自我评价',
    `education_json` JSON DEFAULT NULL COMMENT '教育背景：[{school, major, degree, startDate, endDate}]',
    `work_years` TINYINT DEFAULT NULL COMMENT '工作年限',
    `is_default` TINYINT DEFAULT 0 COMMENT '是否为默认简历：0-否 1-是',
    `version` INT DEFAULT 1 COMMENT '版本号（简历更新次数）',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted` TINYINT NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_parse_status` (`parse_status`),
    KEY `idx_job_title` (`job_title`),
    CONSTRAINT `fk_resume_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='简历表';

-- 简历技能表（独立存储便于检索和匹配）
CREATE TABLE `resume_skill` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `resume_id` BIGINT UNSIGNED NOT NULL COMMENT '简历ID',
    `skill_name` VARCHAR(64) NOT NULL COMMENT '技能名称',
    `proficiency` TINYINT DEFAULT NULL COMMENT '熟练度：1-入门 2-熟练 3-精通 4-专家',
    `category` VARCHAR(32) DEFAULT NULL COMMENT '分类：语言/框架/工具/数据库等',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_resume_id` (`resume_id`),
    KEY `idx_skill_name` (`skill_name`),
    CONSTRAINT `fk_skill_resume` FOREIGN KEY (`resume_id`) REFERENCES `resume` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='简历技能表';

-- 简历项目经验表
CREATE TABLE `resume_project` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `resume_id` BIGINT UNSIGNED NOT NULL,
    `project_name` VARCHAR(128) NOT NULL COMMENT '项目名称',
    `role` VARCHAR(64) DEFAULT NULL COMMENT '担任角色',
    `start_date` DATE DEFAULT NULL,
    `end_date` DATE DEFAULT NULL,
    `description` TEXT COMMENT '项目描述',
    `responsibilities` TEXT COMMENT '个人职责',
    `technologies` VARCHAR(255) DEFAULT NULL COMMENT '使用技术（逗号分隔）',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_resume_id` (`resume_id`),
    CONSTRAINT `fk_project_resume` FOREIGN KEY (`resume_id`) REFERENCES `resume` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='简历项目经验表';

-- ============================================
-- 3. 面试会话模块（核心）
-- ============================================

-- 面试房间/会话表
CREATE TABLE `interview_session` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `session_code` VARCHAR(32) NOT NULL COMMENT '房间号/会话码（如：IV-20240324-XXXX）',
    `candidate_id` BIGINT UNSIGNED NOT NULL COMMENT '面试者用户ID',
    `resume_id` BIGINT UNSIGNED NOT NULL COMMENT '使用的简历ID',
    `hr_id` BIGINT UNSIGNED DEFAULT NULL COMMENT '负责HR用户ID（可选，分配后）',
    `job_position` VARCHAR(128) NOT NULL COMMENT '面试职位',
    `status` VARCHAR(20) NOT NULL DEFAULT 'CREATED' COMMENT '状态：CREATED-创建 ONGOING-进行中 PAUSED-暂停 COMPLETED-完成 TERMINATED-终止',
    `ai_model` VARCHAR(32) DEFAULT 'GPT-4' COMMENT '使用的AI模型',
    `interview_type` TINYINT DEFAULT 1 COMMENT '面试类型：1-技术面试 2-综合面试 3-HR面试',
    `difficulty_level` TINYINT DEFAULT 2 COMMENT '难度等级：1-简单 2-中等 3-困难',
    `expected_duration` INT DEFAULT 30 COMMENT '预计时长（分钟）',
    `scheduled_time` DATETIME DEFAULT NULL COMMENT '预约时间',
    `actual_start_time` DATETIME DEFAULT NULL COMMENT '实际开始时间',
    `actual_end_time` DATETIME DEFAULT NULL COMMENT '实际结束时间',
    `duration_seconds` INT DEFAULT 0 COMMENT '实际耗时（秒）',
    `live_room_id` VARCHAR(64) DEFAULT NULL COMMENT '直播房间ID（WebRTC或三方服务）',
    `is_live_active` TINYINT DEFAULT 0 COMMENT '直播是否进行中',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted` TINYINT DEFAULT 0,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_session_code` (`session_code`),
    KEY `idx_candidate_id` (`candidate_id`),
    KEY `idx_hr_id` (`hr_id`),
    KEY `idx_status` (`status`),
    KEY `idx_created_at` (`created_at`),
    CONSTRAINT `fk_session_candidate` FOREIGN KEY (`candidate_id`) REFERENCES `sys_user` (`id`),
    CONSTRAINT `fk_session_resume` FOREIGN KEY (`resume_id`) REFERENCES `resume` (`id`),
    CONSTRAINT `fk_session_hr` FOREIGN KEY (`hr_id`) REFERENCES `sys_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='面试会话表';

-- ============================================
-- 4. 面试内容模块（Q&A）
-- ============================================

-- 面试题目表（AI生成 + HR修改）
CREATE TABLE `interview_question` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `session_id` BIGINT UNSIGNED NOT NULL COMMENT '所属会话',
    `sequence_no` INT NOT NULL COMMENT '题号（顺序）',
    `source` VARCHAR(20) NOT NULL DEFAULT 'AI' COMMENT '来源：AI-AI生成 HR-HR录入 HR_MODIFIED-HR修改AI',
    `question_type` TINYINT NOT NULL DEFAULT 1 COMMENT '类型：1-技术问题 2-项目经验 3-情景题 4-基础素质 5-算法题',
    `original_content` TEXT COMMENT 'AI原始生成内容',
    `content` TEXT NOT NULL COMMENT '最终展示内容（HR可能修改）',
    `reference_answer` TEXT DEFAULT NULL COMMENT '参考答案',
    `knowledge_points` VARCHAR(255) DEFAULT NULL COMMENT '考察知识点（JSON或逗号分隔）',
    `difficulty` TINYINT DEFAULT 2 COMMENT '难度：1-3',
    `expected_duration` INT DEFAULT 300 COMMENT '建议回答时长（秒）',
    `modified_by` BIGINT UNSIGNED DEFAULT NULL COMMENT '修改人HR ID',
    `modified_at` DATETIME DEFAULT NULL,
    `modify_reason` VARCHAR(255) DEFAULT NULL COMMENT '修改原因',
    `status` VARCHAR(20) DEFAULT 'PENDING' COMMENT '状态',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_session_seq` (`session_id`, `sequence_no`),
    KEY `idx_session_id` (`session_id`),
    KEY `idx_source` (`source`),
    CONSTRAINT `fk_question_session` FOREIGN KEY (`session_id`) REFERENCES `interview_session` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_question_modifier` FOREIGN KEY (`modified_by`) REFERENCES `sys_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='面试题目表';

-- 面试回答与评分表（实时记录）
CREATE TABLE `interview_answer` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `session_id` BIGINT UNSIGNED NOT NULL,
    `question_id` BIGINT UNSIGNED NOT NULL COMMENT '关联题目',
    `candidate_id` BIGINT UNSIGNED NOT NULL,
    `content_text` TEXT COMMENT '文字回答内容',
    `audio_url` VARCHAR(255) DEFAULT NULL COMMENT '语音回答URL（如有语音面试）',
    `audio_duration` INT DEFAULT 0 COMMENT '语音时长（秒）',
    `code_content` TEXT DEFAULT NULL COMMENT '代码作答内容（编程题）',
    `code_language` VARCHAR(32) DEFAULT NULL COMMENT '编程语言',
    `start_time` DATETIME NOT NULL COMMENT '开始作答时间',
    `end_time` DATETIME DEFAULT NULL COMMENT '结束作答时间',
    `thinking_seconds` INT DEFAULT 0 COMMENT '思考时长',
    `answer_seconds` INT DEFAULT 0 COMMENT '回答时长',
    `ai_evaluation` TEXT DEFAULT NULL COMMENT 'AI评价内容（结构化JSON）',
    `ai_score` DECIMAL(3,1) DEFAULT NULL COMMENT 'AI评分（0-10）',
    `ai_dimensions` JSON DEFAULT NULL COMMENT '多维度评分：{technical:8.5,communication:9.0,logic:8.0}',
    `status` VARCHAR(20) DEFAULT 'ANSWERING',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_question` (`question_id`),
    KEY `idx_session_id` (`session_id`),
    KEY `idx_candidate_id` (`candidate_id`),
    KEY `idx_status` (`status`),
    CONSTRAINT `fk_answer_session` FOREIGN KEY (`session_id`) REFERENCES `interview_session` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_answer_question` FOREIGN KEY (`question_id`) REFERENCES `interview_question` (`id`),
    CONSTRAINT `fk_answer_candidate` FOREIGN KEY (`candidate_id`) REFERENCES `sys_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='面试回答表';

-- ============================================
-- 5. 面试报告模块
-- ============================================

-- 面试综合报告表（AI生成）
CREATE TABLE `interview_report` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `session_id` BIGINT UNSIGNED NOT NULL,
    `candidate_id` BIGINT UNSIGNED NOT NULL,
    `hr_id` BIGINT UNSIGNED DEFAULT NULL COMMENT '处理HR',
    `total_score` DECIMAL(4,2) NOT NULL COMMENT '综合得分（0-100）',
    `technical_score` DECIMAL(4,2) DEFAULT NULL COMMENT '技术能力',
    `communication_score` DECIMAL(4,2) DEFAULT NULL COMMENT '沟通能力',
    `logic_score` DECIMAL(4,2) DEFAULT NULL COMMENT '逻辑思维',
    `culture_fit_score` DECIMAL(4,2) DEFAULT NULL COMMENT '文化匹配度',
    `learning_ability_score` DECIMAL(4,2) DEFAULT NULL COMMENT '学习能力',
    `strengths` TEXT COMMENT '优势亮点',
    `weaknesses` TEXT COMMENT '待提升点',
    `technical_assessment` TEXT COMMENT '技术能力评估',
    `overall_evaluation` TEXT COMMENT '综合评价',
    `recommendation` TINYINT DEFAULT 0 COMMENT 'AI建议：0-待定 1-强烈推荐 2-推荐 3-一般 4-不推荐',
    `qa_summary` JSON DEFAULT NULL COMMENT '问答摘要：[{question,answer,keyPoints}]',
    `generated_at` DATETIME NOT NULL,
    `generated_by_ai` TINYINT DEFAULT 1 COMMENT '是否AI生成',
    `hr_reviewed` TINYINT DEFAULT 0 COMMENT 'HR是否已查看',
    `hr_reviewed_at` DATETIME DEFAULT NULL,
    `hr_comments` TEXT DEFAULT NULL COMMENT 'HR补充评价',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_session` (`session_id`),
    KEY `idx_candidate_id` (`candidate_id`),
    KEY `idx_hr_reviewed` (`hr_reviewed`),
    KEY `idx_recommendation` (`recommendation`),
    CONSTRAINT `fk_report_session` FOREIGN KEY (`session_id`) REFERENCES `interview_session` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_report_candidate` FOREIGN KEY (`candidate_id`) REFERENCES `sys_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='面试报告表';

-- ============================================
-- 6. 系统管理模块
-- ============================================

-- 系统配置表（全局配置、AI参数等）
CREATE TABLE `system_config` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `config_key` VARCHAR(64) NOT NULL COMMENT '配置键',
    `config_value` TEXT COMMENT '配置值',
    `description` VARCHAR(255) DEFAULT NULL COMMENT '描述',
    `category` VARCHAR(32) DEFAULT 'GENERAL' COMMENT '分类：GENERAL/AI/MCP/INTERVIEW',
    `editable` TINYINT DEFAULT 1 COMMENT '是否可编辑',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_key` (`config_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统配置表';

-- 角色表
CREATE TABLE `sys_role` (
    `role_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '角色 ID',
    `role_name` VARCHAR(64) NOT NULL COMMENT '角色名称',
    `role_key` VARCHAR(64) NOT NULL COMMENT '角色权限字符串',
    `status` CHAR(1) NOT NULL DEFAULT '0' COMMENT '角色状态（0 正常 1 停用）',
    `remark` VARCHAR(500) DEFAULT '' COMMENT '备注',
    `flag` BOOLEAN DEFAULT false COMMENT '是否选中',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`role_id`),
    UNIQUE KEY `uk_role_key` (`role_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- ============================================
-- 7. 初始化数据
-- ============================================

-- 插入默认角色
INSERT INTO `sys_role` (`role_id`, `role_name`, `role_key`, `status`, `remark`) VALUES
(1, '面试者', 'candidate', '0', '普通面试者角色'),
(2, 'HR', 'hr', '0', '人力资源角色'),
(3, '管理员', 'admin', '0', '系统管理员角色');

-- 插入默认管理员（密码需替换为加密后的值，如BCrypt）
INSERT INTO `sys_user` (`user_name`, `email`, `password`, `real_name`, `role`, `status`) VALUES
('admin', 'admin@ai-interview.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EO', '系统管理员', 3, 1);

-- 菜单权限分配
-- 管理员（role_id=3）：可以查看系统管理（包含用户管理、角色管理、菜单管理）和面试管理
INSERT IGNORE INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES
(3, 1), (3, 2), (3, 3), (3, 4);

-- HR（role_id=2）：可以查看用户管理和面试管理
INSERT IGNORE INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES
(2, 1), (2, 2), (2, 5), (2, 6), (2, 7);

-- 面试者（role_id=1）：只能查看面试管理
INSERT IGNORE INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES
(1, 5), (1, 6), (1, 7);

-- 插入系统配置示例
INSERT INTO `system_config` (`config_key`, `config_value`, `description`, `category`) VALUES
('ai.model.default', 'GPT-4', '默认AI模型', 'AI'),
('ai.temperature', '0.7', 'AI创造性参数（0-1）', 'AI'),
('interview.question.count', '10', '默认面试题目数量', 'INTERVIEW'),
('interview.duration.max', '60', '最大面试时长（分钟）', 'INTERVIEW');
