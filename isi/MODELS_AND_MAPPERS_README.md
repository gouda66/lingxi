# AI 智能面试系统 - 实体类与 Mapper 说明

## 概述

本文档说明了基于 MVC 和 MyBatis-Plus 框架创建的所有实体类和 Mapper 接口。

## 技术栈

- **框架**: Spring Boot 3.5.12
- **ORM**: MyBatis-Plus 3.5.9
- **数据库**: MySQL 8.0+
- **JDK**: 21
- **Lombok**: 简化实体类代码

## 模块划分

### 1. 用户与权限模块

| 实体类 | 表名 | 说明 |
|--------|------|------|
| SysUser | sys_user | 用户基础表（面试者、HR、管理员） |
| HrProfile | hr_profile | HR 扩展信息表 |

### 2. 简历管理模块

| 实体类 | 表名 | 说明 |
|--------|------|------|
| Resume | resume | 简历主表 |
| ResumeSkill | resume_skill | 简历技能表 |
| ResumeProject | resume_project | 简历项目经验表 |

### 3. 面试会话模块（核心）

| 实体类 | 表名 | 说明 |
|--------|------|------|
| InterviewSession | interview_session | 面试会话表 |
| HrObservation | hr_observation | HR 观看记录表 |

### 4. 面试内容模块（Q&A）

| 实体类 | 表名 | 说明 |
|--------|------|------|
| InterviewQuestion | interview_question | 面试题目表 |
| InterviewAnswer | interview_answer | 面试回答表 |
| HrIntervention | hr_intervention | HR 干预记录表 |

### 5. 面试报告与决策模块

| 实体类 | 表名 | 说明 |
|--------|------|------|
| InterviewReport | interview_report | 面试综合报告表 |
| InterviewDecision | interview_decision | 面试决策记录表 |

### 6. MCP 邮件通知模块

| 实体类 | 表名 | 说明 |
|--------|------|------|
| EmailRecord | email_record | 邮件发送记录表 |
| EmailTemplate | email_template | 邮件模板表 |

### 7. 系统管理与日志模块

| 实体类 | 表名 | 说明 |
|--------|------|------|
| SystemConfig | system_config | 系统配置表 |
| OperationLog | operation_log | 操作日志表 |
| AiInvocationLog | ai_invocation_log | AI 调用日志表 |

## 实体类特性

所有实体类都具备以下特性：

1. **Lombok 注解**: 使用 `@Data` 自动生成 getter/setter
2. **MyBatis-Plus 注解**: 
   - `@TableName`: 指定表名
   - `@TableId`: 指定主键及生成策略（自增）
   - `@TableLogic`: 逻辑删除字段
   - `@TableField`: 字段填充策略（自动填充 created_at/updated_at）
3. **序列化**: 实现 `Serializable` 接口
4. **链式调用**: 支持 `@Accessors(chain = true)`

## Mapper 接口

所有 Mapper 接口都：
- 继承 `BaseMapper<T>`，获得基础的 CRUD 能力
- 标注 `@Mapper` 注解，被 Spring 扫描并注册为 Bean

## 配置类

### MybatisPlusConfig

位置：`com.lingxi.isi.config.MybatisPlusConfig`

功能：
1. 配置分页插件（支持 MySQL 分页）
2. 配置自动填充处理器（自动填充 createdAt/updatedAt）

## 字段类型映射说明

| 数据库类型 | Java 类型 | 说明 |
|-----------|----------|------|
| BIGINT | Long | 主键 ID |
| VARCHAR | String | 字符串字段 |
| TEXT | String | 长文本 |
| DATETIME | LocalDateTime | 日期时间 |
| DATE | LocalDate | 日期 |
| TINYINT | Integer | 小整数（状态、标志位） |
| INT | Integer | 整数 |
| DECIMAL | BigDecimal | 精确小数（评分、金额） |
| JSON | String | JSON 数据（存储为字符串） |

## 特殊字段处理

### JSON 字段

以下表的 JSON 字段在实体类中使用 `String` 类型存储：
- `Resume.educationJson`: 教育背景 JSON 数组
- `InterviewAnswer.aiDimensions`: 多维度评分 JSON
- `InterviewReport.qaSummary`: 问答摘要 JSON
- `EmailTemplate.variables`: 可用变量列表 JSON
- `EmailTemplate.mcpConfig`: MCP 配置 JSON

### 逻辑删除

以下表支持逻辑删除（`deleted` 字段）：
- sys_user
- resume
- interview_session

## 自动填充字段

以下字段会在插入/更新时自动填充：
- `createdAt`: 插入时自动填充当前时间
- `updatedAt`: 插入和更新时自动填充当前时间

## 下一步工作

建议后续实现：

1. **Service 层**: 创建业务逻辑服务类
2. **Controller 层**: 创建 RESTful API 控制器
3. **DTO/VO**: 创建数据传输对象和视图对象
4. **XML Mapper**: 如需复杂 SQL，可创建 XML 映射文件
5. **单元测试**: 为 Mapper 和 Service 编写测试用例

## 注意事项

1. 确保数据库连接配置正确（application.yaml）
2. 确保已执行 isi.sql 脚本创建表结构
3. Lombok 插件需要在 IDE 中安装并启用注解处理
4. MyBatis-Plus 已替换原有的 MyBatis 依赖
