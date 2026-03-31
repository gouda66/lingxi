-- AI智能面试系统数据库设计
-- 基于Spring AI + MCP架构
-- MySQL 8.0+ 语法

-- 设置字符集

-- ============================================
-- 1. 用户与权限模块
-- ============================================

-- 用户基础表（面试者、HR、管理员）
CREATE TABLE `sys_user` (
                            `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                            `username` VARCHAR(64) NOT NULL COMMENT '用户名',
                            `email` VARCHAR(128) NULL COMMENT '邮箱（唯一）',
                            `password` VARCHAR(128) NOT NULL COMMENT '加密密码',
                            `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
                            `real_name` VARCHAR(64) DEFAULT NULL COMMENT '真实姓名',
                            `avatar_url` VARCHAR(255) DEFAULT NULL COMMENT '头像URL',
                            `role` TINYINT NOT NULL DEFAULT 1 COMMENT '角色：1-面试者 2-HR 3-管理员',
                            `status` TINYINT NOT NULL DEFAULT 0 COMMENT '状态：0-正常 1-禁用',
                            `last_login_time` DATETIME DEFAULT NULL COMMENT '最后登录时间',
                            `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                            `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                            `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除',
                            `sex` TINYINT DEFAULT 0 COMMENT '性别：0-未知 1-男 2-女',
                            PRIMARY KEY (`id`),
                            UNIQUE KEY `uk_email` (`email`),
                            UNIQUE KEY `uk_username` (`username`),
                            KEY `idx_role_status` (`role`, `status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 在 isi.sql 中添加
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
    `visible` TINYINT DEFAULT 1 COMMENT '菜单状态（0 显示 1 隐藏）',
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
                                                                                                                                                      (1, '系统管理', 0, 1, 'system', NULL, 'M', 1, '0', 'system:menu', 'monitor'),
                                                                                                                                                      (2, '用户管理', 1, 1, 'user', 'system/user/index', 'C', 1, '0', 'system:user:list', 'user'),
                                                                                                                                                      (3, '角色管理', 1, 2, 'role', 'system/role/index', 'C', 1, '0', 'system:role:list', 'peoples'),
                                                                                                                                                      (4, '菜单管理', 1, 3, 'menu', 'system/menu/index', 'C', 1, '0', 'system:menu:list', 'tree-table'),
                                                                                                                                                      (5, '面试管理', 0, 2, 'interview', NULL, 'M', 1, '0', 'interview:menu', 'skill'),
                                                                                                                                                      (6, '面试会话', 5, 1, 'session', 'interview/session/index', 'C', 1, '0', 'interview:session:list', 'eye-open'),
                                                                                                                                                      (7, '简历管理', 5, 2, 'resume', 'interview/resume/index', 'C', 1, '0', 'interview:resume:list', 'star');

-- 分配所有菜单给管理员角色（假设管理员角色 ID=1）
INSERT IGNORE INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES
                                                       (1, 1), (1, 2), (1, 3), (1, 4), (1, 5), (1, 6), (1, 7);


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




-- HR扩展信息表
CREATE TABLE `hr_profile` (
                              `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
                              `user_id` BIGINT UNSIGNED NOT NULL COMMENT '关联用户ID',
                              `company_name` VARCHAR(128) NOT NULL COMMENT '公司名称',
                              `company_id` BIGINT UNSIGNED DEFAULT NULL COMMENT '公司ID（如有企业库）',
                              `department` VARCHAR(64) DEFAULT NULL COMMENT '部门',
                              `position` VARCHAR(64) DEFAULT NULL COMMENT '职位',
                              `work_email` VARCHAR(128) DEFAULT NULL COMMENT '工作邮箱',
                              `notification_enabled` TINYINT DEFAULT 1 COMMENT '是否接收通知',
                              `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
                              `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                              PRIMARY KEY (`id`),
                              UNIQUE KEY `uk_user_id` (`user_id`),
                              KEY `idx_company` (`company_id`),
                              CONSTRAINT `fk_hr_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='HR信息表';

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

    -- 基础信息（解析后结构化存储）
                          `full_name` VARCHAR(64) DEFAULT NULL COMMENT '姓名',
                          `gender` TINYINT DEFAULT NULL COMMENT '性别：0-女 1-男',
                          `birth_date` DATE DEFAULT NULL COMMENT '出生日期',
                          `phone` VARCHAR(20) DEFAULT NULL COMMENT '电话',
                          `email` VARCHAR(128) DEFAULT NULL COMMENT '邮箱',
                          `location` VARCHAR(128) DEFAULT NULL COMMENT '所在地',
                          `job_title` VARCHAR(128) DEFAULT NULL COMMENT '求职意向/目标职位',
                          `expected_salary` VARCHAR(64) DEFAULT NULL COMMENT '期望薪资',
                          `self_evaluation` TEXT DEFAULT NULL COMMENT '自我评价',

    -- 教育背景JSON存储（支持多段经历）
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

    -- 状态流转：CREATED -> ONGOING -> PAUSED -> COMPLETED / TERMINATED
                                     `status` VARCHAR(20) NOT NULL DEFAULT 'CREATED' COMMENT '状态：CREATED-创建 ONGOING-进行中 PAUSED-暂停 COMPLETED-完成 TERMINATED-终止',

    -- AI配置
                                     `ai_model` VARCHAR(32) DEFAULT 'GPT-4' COMMENT '使用的AI模型',
                                     `interview_type` TINYINT DEFAULT 1 COMMENT '面试类型：1-技术面试 2-综合面试 3-HR面试',
                                     `difficulty_level` TINYINT DEFAULT 2 COMMENT '难度等级：1-简单 2-中等 3-困难',
                                     `expected_duration` INT DEFAULT 30 COMMENT '预计时长（分钟）',

    -- 时间记录
                                     `scheduled_time` DATETIME DEFAULT NULL COMMENT '预约时间',
                                     `actual_start_time` DATETIME DEFAULT NULL COMMENT '实际开始时间',
                                     `actual_end_time` DATETIME DEFAULT NULL COMMENT '实际结束时间',
                                     `duration_seconds` INT DEFAULT 0 COMMENT '实际耗时（秒）',

    -- 直播/实时相关
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

-- HR观看记录表（支持多个HR同时观看）
CREATE TABLE `hr_observation` (
                                  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
                                  `session_id` BIGINT UNSIGNED NOT NULL COMMENT '面试会话ID',
                                  `hr_id` BIGINT UNSIGNED NOT NULL COMMENT 'HR用户ID',
                                  `join_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '进入时间',
                                  `leave_time` DATETIME DEFAULT NULL COMMENT '离开时间',
                                  `duration_seconds` INT DEFAULT 0 COMMENT '观看时长',
                                  `is_active` TINYINT DEFAULT 1 COMMENT '是否在线',
                                  `ip_address` VARCHAR(32) DEFAULT NULL,
                                  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
                                  PRIMARY KEY (`id`),
                                  UNIQUE KEY `uk_session_hr_active` (`session_id`, `hr_id`, `is_active`),
                                  KEY `idx_session_id` (`session_id`),
                                  CONSTRAINT `fk_obs_session` FOREIGN KEY (`session_id`) REFERENCES `interview_session` (`id`) ON DELETE CASCADE,
                                  CONSTRAINT `fk_obs_hr` FOREIGN KEY (`hr_id`) REFERENCES `sys_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='HR观看记录表';

-- ============================================
-- 4. 面试内容模块（Q&A）
-- ============================================

-- 面试题目表（AI生成 + HR修改）
CREATE TABLE `interview_question` (
                                      `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
                                      `session_id` BIGINT UNSIGNED NOT NULL COMMENT '所属会话',
                                      `sequence_no` INT NOT NULL COMMENT '题号（顺序）',

    -- 题目来源与状态
                                      `source` VARCHAR(20) NOT NULL DEFAULT 'AI' COMMENT '来源：AI-AI生成 HR-HR录入 HR_MODIFIED-HR修改AI',
                                      `question_type` TINYINT NOT NULL DEFAULT 1 COMMENT '类型：1-技术问题 2-项目经验 3-情景题 4-基础素质 5-算法题',

    -- 题目内容（支持版本控制）
                                      `original_content` TEXT COMMENT 'AI原始生成内容',
                                      `content` TEXT NOT NULL COMMENT '最终展示内容（HR可能修改）',
                                      `reference_answer` TEXT DEFAULT NULL COMMENT '参考答案',
                                      `knowledge_points` VARCHAR(255) DEFAULT NULL COMMENT '考察知识点（JSON或逗号分隔）',
                                      `difficulty` TINYINT DEFAULT 2 COMMENT '难度：1-3',
                                      `expected_duration` INT DEFAULT 300 COMMENT '建议回答时长（秒）',

    -- HR修改记录
                                      `modified_by` BIGINT UNSIGNED DEFAULT NULL COMMENT '修改人HR ID',
                                      `modified_at` DATETIME DEFAULT NULL,
                                      `modify_reason` VARCHAR(255) DEFAULT NULL COMMENT '修改原因',

    -- 状态：PENDING -> ASKED -> ANSWERED -> EVALUATED
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

    -- 回答内容（多模态支持）
                                    `content_text` TEXT COMMENT '文字回答内容',
                                    `audio_url` VARCHAR(255) DEFAULT NULL COMMENT '语音回答URL（如有语音面试）',
                                    `audio_duration` INT DEFAULT 0 COMMENT '语音时长（秒）',
                                    `code_content` TEXT DEFAULT NULL COMMENT '代码作答内容（编程题）',
                                    `code_language` VARCHAR(32) DEFAULT NULL COMMENT '编程语言',

    -- 时间记录
                                    `start_time` DATETIME NOT NULL COMMENT '开始作答时间',
                                    `end_time` DATETIME DEFAULT NULL COMMENT '结束作答时间',
                                    `thinking_seconds` INT DEFAULT 0 COMMENT '思考时长',
                                    `answer_seconds` INT DEFAULT 0 COMMENT '回答时长',

    -- AI实时评估（流式更新）
                                    `ai_evaluation` TEXT DEFAULT NULL COMMENT 'AI评价内容（结构化JSON）',
                                    `ai_score` DECIMAL(3,1) DEFAULT NULL COMMENT 'AI评分（0-10）',
                                    `ai_dimensions` JSON DEFAULT NULL COMMENT '多维度评分：{technical:8.5,communication:9.0,logic:8.0}',

    -- 状态：ANSWERING -> EVALUATING -> COMPLETED
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

-- HR干预记录表（HR修改题目、追加提问等）
CREATE TABLE `hr_intervention` (
                                   `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
                                   `session_id` BIGINT UNSIGNED NOT NULL,
                                   `hr_id` BIGINT UNSIGNED NOT NULL,
                                   `intervention_type` VARCHAR(32) NOT NULL COMMENT '干预类型：MODIFY_QUESTION-修改题目 ADD_QUESTION-追加题目 SKIP_QUESTION-跳过题目 HINT-提示',
                                   `target_question_id` BIGINT UNSIGNED DEFAULT NULL COMMENT '目标题目ID',
                                   `before_content` TEXT DEFAULT NULL COMMENT '修改前内容',
                                   `after_content` TEXT DEFAULT NULL COMMENT '修改后内容',
                                   `remark` VARCHAR(255) DEFAULT NULL COMMENT '备注/原因',
                                   `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                   PRIMARY KEY (`id`),
                                   KEY `idx_session_id` (`session_id`),
                                   KEY `idx_hr_id` (`hr_id`),
                                   CONSTRAINT `fk_intervention_session` FOREIGN KEY (`session_id`) REFERENCES `interview_session` (`id`) ON DELETE CASCADE,
                                   CONSTRAINT `fk_intervention_hr` FOREIGN KEY (`hr_id`) REFERENCES `sys_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='HR干预记录表';

-- ============================================
-- 5. 面试报告与决策模块
-- ============================================

-- 面试综合报告表（AI生成）
CREATE TABLE `interview_report` (
                                    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
                                    `session_id` BIGINT UNSIGNED NOT NULL,
                                    `candidate_id` BIGINT UNSIGNED NOT NULL,
                                    `hr_id` BIGINT UNSIGNED DEFAULT NULL COMMENT '处理HR',

    -- 综合评分
                                    `total_score` DECIMAL(4,2) NOT NULL COMMENT '综合得分（0-100）',
                                    `technical_score` DECIMAL(4,2) DEFAULT NULL COMMENT '技术能力',
                                    `communication_score` DECIMAL(4,2) DEFAULT NULL COMMENT '沟通能力',
                                    `logic_score` DECIMAL(4,2) DEFAULT NULL COMMENT '逻辑思维',
                                    `culture_fit_score` DECIMAL(4,2) DEFAULT NULL COMMENT '文化匹配度',
                                    `learning_ability_score` DECIMAL(4,2) DEFAULT NULL COMMENT '学习能力',

    -- 详细评价
                                    `strengths` TEXT COMMENT '优势亮点',
                                    `weaknesses` TEXT COMMENT '待提升点',
                                    `technical_assessment` TEXT COMMENT '技术能力评估',
                                    `overall_evaluation` TEXT COMMENT '综合评价',
                                    `recommendation` TINYINT DEFAULT 0 COMMENT 'AI建议：0-待定 1-强烈推荐 2-推荐 3-一般 4-不推荐',

    -- 问答摘要（关键QA）
                                    `qa_summary` JSON DEFAULT NULL COMMENT '问答摘要：[{question,answer,keyPoints}]',

    -- 报告生成时间
                                    `generated_at` DATETIME NOT NULL,
                                    `generated_by_ai` TINYINT DEFAULT 1 COMMENT '是否AI生成',

    -- HR确认
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

-- 面试决策记录表（HR操作）
CREATE TABLE `interview_decision` (
                                      `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
                                      `session_id` BIGINT UNSIGNED NOT NULL,
                                      `report_id` BIGINT UNSIGNED NOT NULL,
                                      `hr_id` BIGINT UNSIGNED NOT NULL COMMENT '决策HR',
                                      `decision` VARCHAR(20) NOT NULL COMMENT '决策：PASS-通过 REJECT-拒绝 PENDING-待定',
                                      `decision_reason` TEXT COMMENT '决策理由',
                                      `next_round` TINYINT DEFAULT 0 COMMENT '是否进入下一轮：0-否 1-是',
                                      `next_round_desc` VARCHAR(255) DEFAULT NULL COMMENT '下一轮安排说明',
                                      `salary_offer` VARCHAR(64) DEFAULT NULL COMMENT '建议薪资（如通过）',
                                      `send_notification` TINYINT DEFAULT 1 COMMENT '是否发送通知邮件',
                                      `decided_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                      `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
                                      PRIMARY KEY (`id`),
                                      UNIQUE KEY `uk_session` (`session_id`),
                                      KEY `idx_decision` (`decision`),
                                      KEY `idx_hr_id` (`hr_id`),
                                      CONSTRAINT `fk_decision_session` FOREIGN KEY (`session_id`) REFERENCES `interview_session` (`id`) ON DELETE CASCADE,
                                      CONSTRAINT `fk_decision_report` FOREIGN KEY (`report_id`) REFERENCES `interview_report` (`id`),
                                      CONSTRAINT `fk_decision_hr` FOREIGN KEY (`hr_id`) REFERENCES `sys_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='面试决策表';

-- ============================================
-- 6. MCP邮件通知模块
-- ============================================

-- 邮件发送记录表（MCP调用日志）
CREATE TABLE `email_record` (
                                `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
                                `template_code` VARCHAR(64) NOT NULL COMMENT '邮件模板编码：INTERVIEW_PASS-面试通过等',
                                `recipient_email` VARCHAR(128) NOT NULL COMMENT '收件人邮箱',
                                `recipient_name` VARCHAR(64) DEFAULT NULL COMMENT '收件人姓名',
                                `subject` VARCHAR(255) NOT NULL COMMENT '邮件主题',
                                `content_html` TEXT COMMENT '邮件HTML内容',
                                `content_text` TEXT COMMENT '邮件纯文本内容',

    -- 关联业务
                                `related_type` VARCHAR(32) DEFAULT NULL COMMENT '关联业务类型：INTERVIEW_DECISION/SYSTEM_NOTICE',
                                `related_id` BIGINT DEFAULT NULL COMMENT '关联业务ID',
                                `session_id` BIGINT UNSIGNED DEFAULT NULL,

    -- MCP调用信息
                                `mcp_request_id` VARCHAR(64) DEFAULT NULL COMMENT 'MCP请求ID',
                                `mcp_provider` VARCHAR(32) DEFAULT 'SENDGRID' COMMENT '邮件服务商：SENDGRID/AWS_SES/ALIYUN',

    -- 发送状态
                                `status` VARCHAR(20) DEFAULT 'PENDING' COMMENT '状态：PENDING-待发送 SENDING-发送中 SENT-已发送 FAILED-失败',
                                `sent_at` DATETIME DEFAULT NULL COMMENT '实际发送时间',
                                `error_msg` TEXT DEFAULT NULL COMMENT '错误信息',
                                `retry_count` TINYINT DEFAULT 0 COMMENT '重试次数',

    -- 打开追踪（可选）
                                `opened` TINYINT DEFAULT 0 COMMENT '是否已打开',
                                `opened_at` DATETIME DEFAULT NULL,

                                `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                PRIMARY KEY (`id`),
                                KEY `idx_template` (`template_code`),
                                KEY `idx_recipient` (`recipient_email`),
                                KEY `idx_status` (`status`),
                                KEY `idx_session` (`session_id`),
                                KEY `idx_related` (`related_type`, `related_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='邮件发送记录表';

-- 邮件模板表（管理员配置）
CREATE TABLE `email_template` (
                                  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
                                  `template_code` VARCHAR(64) NOT NULL COMMENT '模板编码（唯一）',
                                  `template_name` VARCHAR(128) NOT NULL COMMENT '模板名称',
                                  `subject` VARCHAR(255) NOT NULL COMMENT '邮件主题（支持变量：{{userName}}）',
                                  `content_html` TEXT NOT NULL COMMENT 'HTML模板',
                                  `content_text` TEXT COMMENT '纯文本模板',
                                  `variables` JSON DEFAULT NULL COMMENT '可用变量列表',
                                  `mcp_config` JSON DEFAULT NULL COMMENT 'MCP特定配置（如SendGrid模板ID）',
                                  `enabled` TINYINT DEFAULT 1,
                                  `created_by` BIGINT UNSIGNED DEFAULT NULL,
                                  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
                                  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                  PRIMARY KEY (`id`),
                                  UNIQUE KEY `uk_code` (`template_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='邮件模板表';

-- ============================================
-- 7. 系统管理与日志模块
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

-- 操作日志表（审计用）
CREATE TABLE `operation_log` (
                                 `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
                                 `user_id` BIGINT UNSIGNED DEFAULT NULL COMMENT '操作用户',
                                 `user_role` TINYINT DEFAULT NULL COMMENT '用户角色',
                                 `operation_type` VARCHAR(32) NOT NULL COMMENT '操作类型：LOGIN/CREATE_INTERVIEW/UPDATE_RESUME/SEND_EMAIL等',
                                 `operation_desc` VARCHAR(255) DEFAULT NULL COMMENT '操作描述',
                                 `request_method` VARCHAR(10) DEFAULT NULL COMMENT 'HTTP方法',
                                 `request_url` VARCHAR(255) DEFAULT NULL COMMENT '请求URL',
                                 `request_params` TEXT DEFAULT NULL COMMENT '请求参数（JSON）',
                                 `response_data` TEXT DEFAULT NULL COMMENT '响应数据（JSON）',
                                 `ip_address` VARCHAR(32) DEFAULT NULL,
                                 `user_agent` VARCHAR(255) DEFAULT NULL,
                                 `execution_time` INT DEFAULT NULL COMMENT '执行时长（ms）',
                                 `status` TINYINT DEFAULT 1 COMMENT '状态：0-失败 1-成功',
                                 `error_msg` TEXT DEFAULT NULL,
                                 `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                 PRIMARY KEY (`id`),
                                 KEY `idx_user_id` (`user_id`),
                                 KEY `idx_operation_type` (`operation_type`),
                                 KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志表';

-- AI调用日志表（MCP/AI成本追踪）
CREATE TABLE `ai_invocation_log` (
                                     `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
                                     `session_id` BIGINT UNSIGNED DEFAULT NULL,
                                     `user_id` BIGINT UNSIGNED DEFAULT NULL,
                                     `provider` VARCHAR(32) NOT NULL COMMENT 'AI提供商：OPENAI/ANTHROPIC/LOCAL',
                                     `model` VARCHAR(32) NOT NULL COMMENT '模型名称',
                                     `invocation_type` VARCHAR(32) NOT NULL COMMENT '调用类型：GENERATE_QUESTION/EVALUATE_ANSWER/GENERATE_REPORT',
                                     `prompt_tokens` INT UNSIGNED DEFAULT 0,
                                     `completion_tokens` INT UNSIGNED DEFAULT 0,
                                     `total_tokens` INT UNSIGNED DEFAULT 0,
                                     `cost_usd` DECIMAL(10,6) DEFAULT 0 COMMENT '估算成本（USD）',
                                     `latency_ms` INT DEFAULT NULL COMMENT '响应延迟',
                                     `request_content` MEDIUMTEXT COMMENT '请求内容（脱敏后）',
                                     `response_content` MEDIUMTEXT COMMENT '响应内容',
                                     `status` VARCHAR(20) DEFAULT 'SUCCESS',
                                     `error_msg` TEXT,
                                     `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                     PRIMARY KEY (`id`),
                                     KEY `idx_session_id` (`session_id`),
                                     KEY `idx_invocation_type` (`invocation_type`),
                                     KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI调用日志表';

-- ============================================
-- 8. 初始化数据
-- ============================================

-- 插入默认管理员（密码需替换为加密后的值，如BCrypt）
INSERT INTO `sys_user` (`username`, `email`, `password`, `real_name`, `role`, `status`) VALUES
    ('admin', 'admin@ai-interview.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EO', '系统管理员', 3, 1);

-- 插入系统配置示例
INSERT INTO `system_config` (`config_key`, `config_value`, `description`, `category`) VALUES
                                                                                          ('ai.model.default', 'GPT-4', '默认AI模型', 'AI'),
                                                                                          ('ai.temperature', '0.7', 'AI创造性参数（0-1）', 'AI'),
                                                                                          ('interview.question.count', '10', '默认面试题目数量', 'INTERVIEW'),
                                                                                          ('interview.duration.max', '60', '最大面试时长（分钟）', 'INTERVIEW'),
                                                                                          ('mcp.email.enabled', 'true', '是否启用MCP邮件服务', 'MCP'),
                                                                                          ('mcp.email.provider', 'sendgrid', '邮件服务提供商', 'MCP');

-- 插入邮件模板示例
INSERT INTO `email_template` (`template_code`, `template_name`, `subject`, `content_html`, `variables`) VALUES
                                                                                                            ('INTERVIEW_PASS', '面试通过通知', '{{companyName}}面试通过通知 - {{position}}',
                                                                                                             '<html><body><h2>恭喜您！</h2><p>{{candidateName}}您好，</p><p>您应聘的{{position}}职位已通过面试...</p></body></html>',
                                                                                                             '["candidateName", "position", "companyName", "nextStep"]'),

                                                                                                            ('INTERVIEW_REJECT', '面试结果通知', '{{companyName}}面试结果通知',
                                                                                                             '<html><body><p>{{candidateName}}您好，</p><p>感谢您对{{position}}职位的关注...</p></body></html>',
                                                                                                             '["candidateName", "position", "companyName"]');