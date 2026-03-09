# isi 完整数据库脚本使用指南

## 📋 文件说明

### 两个 SQL 文件的区别

| 文件 | 路径 | 用途 | 特点 |
|------|------|------|------|
| **isi.sql** | `src/main/resources/db/isi.sql` | 基础版 | 仅包含 6 张核心表，适合快速测试 |
| **isi-complete.sql** | `src/main/resources/db/isi-complete.sql` ⭐ | **完整版** | 包含 10 张表 + WebRTC 支持 + 日志审计，**推荐使用** |

---

## 🎯 推荐：使用完整版脚本

### 方式一：命令行执行

```bash
# Windows PowerShell
mysql -u root -p < D:\study_project\lingxi\isi\src\main\resources\db\isi-complete.sql

# Linux/Mac
mysql -u root -p < /path/to/isi-complete.sql
```

### 方式二：MySQL Workbench

1. 打开 MySQL Workbench
2. File → Open SQL Script
3. 选择 `isi-complete.sql`
4. 点击 Execute（闪电图标）

### 方式三：Navicat

1. 打开 Navicat
2. 连接 MySQL
3. 右键 → 运行 SQL 文件
4. 选择 `isi-complete.sql`
5. 开始执行

---

## 🗄️ 完整表结构（10 张表）

### 核心业务表（6 张）

| 序号 | 表名 | 中文名 | 字段数 | 说明 |
|------|------|--------|--------|------|
| 1 | `isi_user` | 用户表 | 17 | 三种角色：USER/HR/ADMIN |
| 2 | `isi_resume` | 简历表 | 24 | 支持 JSON 结构化数据 |
| 3 | `isi_interview_room` | 面试间表 | 22 | **支持 WebRTC SDP/ICE** |
| 4 | `isi_ai_question` | AI 问题表 | 16 | 智能生成面试问题 |
| 5 | `isi_interview_question_record` | 面试记录表 | 16 | 问答记录与评分 |
| 6 | `isi_interview_evaluation` | 评估表 | 18 | **六边形雷达图数据** |

### 增强功能表（4 张）⭐ NEW

| 序号 | 表名 | 中文名 | 字段数 | 说明 |
|------|------|--------|--------|------|
| 7 | `isi_webrtc_session` | WebRTC 会话表 | 17 | **音视频通话记录** |
| 8 | `isi_hr_operation_log` | HR 操作日志表 | 12 | 审计追踪 |
| 9 | `isi_admin_audit_log` | 管理员审计日志表 | 11 | 系统审计 |
| 10 | `isi_system_config` | 系统配置表 | 9 | 动态配置管理 |

---

## ✨ 核心特性

### 1. WebRTC 实时音视频支持 ⭐

**面试间表新增字段**：
```sql
-- WebRTC 信令数据
`webrtc_sdp_offer` TEXT COMMENT 'WebRTC Offer SDP',
`webrtc_sdp_answer` TEXT COMMENT 'WebRTC Answer SDP',
`ice_candidates` JSON COMMENT 'ICE Candidates',

-- 录制功能
`recording_enabled` BOOLEAN DEFAULT FALSE,
`recording_url` VARCHAR(256),

-- 会议管理
`participant_count` INT DEFAULT 0,
`max_participants` INT DEFAULT 10
```

**独立的 WebRTC 会话表**：
```sql
CREATE TABLE `isi_webrtc_session` (
    `sdp_offer` TEXT,
    `sdp_answer` TEXT,
    `ice_candidates_local` JSON,
    `ice_candidates_remote` JSON,
    `connection_status` VARCHAR(32),  -- NEW/CHECKING/CONNECTED/DISCONNECTED
    `quality_score` DECIMAL(5,2),
    `bandwidth_kbps` INT,
    `latency_ms` INT,
    `packet_loss` DECIMAL(5,2)
)
```

### 2. 六边形雷达图评估

**评估表六个维度**：
```sql
`technical_skill` DECIMAL(5,2)   -- 专业技能
`communication` DECIMAL(5,2)      -- 沟通能力
`problem_solving` DECIMAL(5,2)    -- 问题解决
`learning_ability` DECIMAL(5,2)   -- 学习能力
`teamwork` DECIMAL(5,2)           -- 团队协作
`cultural_fit` DECIMAL(5,2)       -- 文化匹配

`total_score` DECIMAL(5,2)        -- 综合总分
`recommendation` VARCHAR(32)      -- 推荐建议
```

### 3. HR 操作审计

**HR 操作日志表**：
```sql
`operation_type` VARCHAR(32)  -- VIEW_RESUME/ADD_QUESTION/START_INTERVIEW等
`target_type` VARCHAR(32)     -- RESUME/QUESTION/INTERVIEW
`operation_detail` JSON       -- 详细操作内容
`ip_address` VARCHAR(64)
`result` VARCHAR(32)          -- SUCCESS/FAILURE
```

### 4. 系统动态配置

**配置表支持热更新**：
```sql
INSERT INTO `isi_system_config` VALUES
('ai.enabled', 'false', '是否启用 AI'),
('webrtc.stun.server', 'stun:stun.l.google.com:19302'),
('interview.default.duration', '60', '默认面试时长'),
('resume.max.file.size', '10485760', '简历最大 10MB');
```

---

## 📊 索引优化

### 已创建的索引（共 35+ 个）

| 表名 | 索引字段 | 类型 | 作用 |
|------|---------|------|------|
| isi_user | username | UNIQUE | 用户名唯一性 |
| isi_user | role, status | NORMAL | 角色/状态筛选 |
| isi_resume | user_id, status | NORMAL | 用户简历查询 |
| isi_resume | expected_position | NORMAL | 职位匹配查询 |
| isi_interview_room | room_id | UNIQUE | 房间号唯一性 |
| isi_interview_room | status, start_time | NORMAL | 面试状态筛选 |
| isi_ai_question | resume_id, category | NORMAL | 问题分类查询 |
| isi_webrtc_session | room_id, connection_status | NORMAL | 会话状态监控 |

---

## 🧪 测试数据

### 默认账号（7 个）

| 用户名 | 密码 | 角色 | 说明 |
|--------|------|------|------|
| admin | admin123 | ADMIN | 系统管理员 |
| hr_test | hr123 | HR | 某某科技公司 HR |
| user_test | user123 | USER | 求职者李小明 |
| hr_tencent | hr123 | HR | 腾讯科技 HR |
| hr_alibaba | hr123 | HR | 阿里巴巴 HR |
| zhangsan | user123 | USER | 求职者张三 |
| lisi | user123 | USER | 求职者李四 |

### 系统配置初始值

```sql
ai.enabled = false              # AI 功能开关
ai.api.key = your-api-key-here  # AI API Key
webrtc.stun.server = stun:stun.l.google.com:19302
interview.default.duration = 60  # 默认 60 分钟
resume.max.file.size = 10485760  # 10MB
```

---

## 🔧 常用 SQL 操作

### 1. 查看所有表

```sql
USE isi_db;
SHOW TABLES;

-- 应该看到 10 张表：
-- isi_user
-- isi_resume
-- isi_interview_room
-- isi_ai_question
-- isi_interview_question_record
-- isi_interview_evaluation
-- isi_webrtc_session
-- isi_hr_operation_log
-- isi_admin_audit_log
-- isi_system_config
```

### 2. 查看表结构

```sql
DESC isi_user;
DESC isi_resume;
DESC isi_interview_room;
DESC isi_webrtc_session;
DESC isi_interview_evaluation;
```

### 3. 查看测试账号

```sql
SELECT id, username, real_name, role, company_name, status 
FROM isi_user;
```

### 4. 查看系统配置

```sql
SELECT config_key, config_value, description 
FROM isi_system_config;
```

### 5. 统计每日面试数量

```sql
SELECT * FROM v_interview_statistics 
ORDER BY interview_date DESC 
LIMIT 30;
```

### 6. 统计简历投递情况

```sql
SELECT * FROM v_resume_statistics 
ORDER BY resume_date DESC 
LIMIT 30;
```

### 7. 查看某个用户的面试评估（雷达图数据）

```sql
SELECT 
    technical_skill,
    communication,
    problem_solving,
    learning_ability,
    teamwork,
    cultural_fit,
    total_score,
    recommendation
FROM isi_interview_evaluation 
WHERE user_id = 3;
```

### 8. 查看某个面试间的 WebRTC 会话质量

```sql
SELECT 
    quality_score,
    bandwidth_kbps,
    latency_ms,
    packet_loss,
    duration_seconds
FROM isi_webrtc_session 
WHERE room_id = 'ROOM-20260309120000-ABC123';
```

### 9. 查看 HR 的操作记录

```sql
SELECT 
    u.real_name,
    o.operation_type,
    o.target_type,
    o.result,
    o.create_time
FROM isi_hr_operation_log o
JOIN isi_user u ON o.hr_user_id = u.id
WHERE u.username = 'hr_test'
ORDER BY o.create_time DESC;
```

---

## ⚠️ 注意事项

### 1. 字符集和时区

```yaml
# application.yaml 必须配置
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/isi_db?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai
```

### 2. MySQL 版本要求

- **最低版本**: MySQL 5.7+
- **推荐版本**: MySQL 8.0+
- **原因**: 需要 JSON 类型支持

### 3. 权限管理（生产环境）

```sql
-- 创建应用用户
CREATE USER 'isi_app'@'localhost' IDENTIFIED BY 'strong_password';
GRANT SELECT, INSERT, UPDATE, DELETE ON isi_db.* TO 'isi_app'@'localhost';

-- 创建只读用户（报表查询）
CREATE USER 'isi_readonly'@'localhost' IDENTIFIED BY 'readonly_password';
GRANT SELECT ON isi_db.* TO 'isi_readonly'@'localhost';

FLUSH PRIVILEGES;
```

### 4. 清空所有数据（慎用！）

```sql
SET FOREIGN_KEY_CHECKS = 0;

TRUNCATE TABLE isi_admin_audit_log;
TRUNCATE TABLE isi_hr_operation_log;
TRUNCATE TABLE isi_webrtc_session;
TRUNCATE TABLE isi_interview_evaluation;
TRUNCATE TABLE isi_interview_question_record;
TRUNCATE TABLE isi_ai_question;
TRUNCATE TABLE isi_interview_room;
TRUNCATE TABLE isi_resume;
TRUNCATE TABLE isi_user;
TRUNCATE TABLE isi_system_config;

SET FOREIGN_KEY_CHECKS = 1;
```

---

## 🚀 快速验证

### 执行脚本后检查

```sql
-- 1. 检查表数量
SELECT COUNT(*) as table_count 
FROM information_schema.tables 
WHERE table_schema = 'isi_db';
-- 应该返回：10

-- 2. 检查用户数量
SELECT COUNT(*) as user_count 
FROM isi_user;
-- 应该返回：7

-- 3. 检查系统配置
SELECT COUNT(*) as config_count 
FROM isi_system_config;
-- 应该返回：8

-- 4. 检查视图
SHOW FULL TABLES WHERE TABLE_TYPE = 'VIEW';
-- 应该显示：v_interview_statistics, v_resume_statistics
```

---

## 📈 性能基准

### 单表数据量建议

| 表名 | 建议上限 | 优化策略 |
|------|---------|---------|
| isi_user | 100 万 | 分区表 |
| isi_resume | 500 万 | 按时间分区 |
| isi_interview_room | 1000 万 | 按月归档 |
| isi_hr_operation_log | 500 万 | 定期清理 |
| isi_webrtc_session | 200 万 | 按天分区 |

### 索引维护

```sql
-- 每月分析一次表
ANALYZE TABLE isi_user;
ANALYZE TABLE isi_resume;
ANALYZE TABLE isi_interview_room;

-- 检查慢查询
SHOW GLOBAL STATUS LIKE 'Slow_queries';
```

---

## 🎉 完成提示

执行成功后，你会看到：

```
✅ 数据库初始化完成！
=====================================
数据库：isi_db
表数量：10 张
  - isi_user (用户表)
  - isi_resume (简历表)
  - isi_interview_room (面试间表) [含 WebRTC 支持]
  - isi_ai_question (AI 问题表)
  - isi_interview_question_record (面试记录表)
  - isi_interview_evaluation (评估表) [六边形雷达图]
  - isi_webrtc_session (WebRTC 会话表) ⭐ NEW
  - isi_hr_operation_log (HR 操作日志表) ⭐ NEW
  - isi_admin_audit_log (管理员审计日志表) ⭐ NEW
  - isi_system_config (系统配置表) ⭐ NEW
=====================================
测试账号:
  管理员：admin / admin123
  HR: hr_test / hr123
  求职者：user_test / user123
=====================================
```

---

## 📚 相关文档

- [DATABASE_SCRIPT.md](./DATABASE_SCRIPT.md) - 基础版数据库说明
- [FEATURES_ENHANCEMENT.md](./FEATURES_ENHANCEMENT.md) - WebRTC 功能增强
- [QUICK_START.md](./QUICK_START.md) - 快速开始指南

---

**数据库脚本准备完毕！** 🎉

现在你可以启动项目，体验完整的面试系统功能了！
