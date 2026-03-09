# 灵曦面试系统 (isi - Interview & Selection Infrastructure)

## 项目简介

基于 DDD（领域驱动设计）架构的求职 + 模拟面试网站，支持三种角色：求职者、企业 HR 和系统管理员。

### 核心功能

1. **简历管理模块**
   - 简历上传（支持 PDF/DOC/DOCX）
   - AI 自动解析简历内容
   - 结构化数据存储
   - 简历状态跟踪

2. **面试管理模块**
   - 创建面试间（类似腾讯会议会议室）
   - 实时面试（WebSocket 通信）
   - HR 可加入面试间
   - 面试过程记录

3. **AI 面试题库模块**
   - AI 根据简历自动生成面试问题
   - 问题类型：技术题、行为题、文化匹配题、通用题
   - HR 可在面试途中查看题库
   - HR 可手动添加、编辑、修改问题

4. **智能评估模块**
   - 面试后 AI 自动评分
   - **六边形雷达图可视化**（六个维度）：
     - 专业技能 (Technical Skill)
     - 沟通能力 (Communication)
     - 问题解决能力 (Problem Solving)
     - 学习能力 (Learning Ability)
     - 团队协作能力 (Teamwork)
     - 文化匹配度 (Cultural Fit)
   - 综合总分计算
   - 优势/劣势分析
   - 推荐建议生成

5. **HR 工作台**
   - 查看简历列表
   - 查看 AI 预设提问题库
   - 面试途中编辑和添加问题
   - 手动调整评分
   - 填写评语

6. **管理员模块**
   - 全局权限管理
   - 查看所有信息
   - 用户管理
   - 系统监控

## 技术栈

- **后端框架**: Spring Boot 4.0.1
- **数据库**: MySQL 8.0+
- **ORM**: Spring Data JPA
- **实时通信**: WebSocket (STOMP 协议)
- **AI 集成**: LangChain4j（支持 OpenAI 等模型）
- **PDF 解析**: Apache PDFBox
- **工具类**: Hutool、Fastjson2
- **构建工具**: Maven

## DDD 架构说明

### 项目结构

```
isi/
├── application/              # 应用层
│   ├── command/             # 命令对象
│   ├── dto/                 # 数据传输对象
│   └── service/             # 应用服务
├── domain/                  # 领域层（核心业务逻辑）
│   ├── model/
│   │   ├── aggregate/       # 聚合根
│   │   └── entity/          # 实体
│   ├── repository/          # 仓储接口
│   └── service/             # 领域服务
├── infrastructure/          # 基础设施层
│   ├── config/              # 配置类
│   ├── filter/              # 过滤器
│   └── repository/
│       ├── impl/            # 仓储实现
│       └── jpa/             # JPA 接口
├── interfaces/              # 接口层
│   ├── facade/              # 门面
│   └── rest/                # REST Controller
└── common/                  # 公共组件
    ├── context/             # 上下文
    ├── exception/           # 异常处理
    ├── result/              # 统一返回结果
    └── util/                # 工具类
```

### 核心领域模型

1. **User（用户）** - 三种角色：USER、HR、ADMIN
2. **Resume（简历）** - 求职者的简历信息
3. **InterviewRoom（面试间）** - 面试房间
4. **AiQuestion（AI 问题）** - AI 生成的面试问题
5. **InterviewQuestionRecord（面试问题记录）** - 面试过程中的问答记录
6. **InterviewEvaluation（面试评估）** - 包含六边形雷达图数据

## 快速开始

### 1. 环境要求

- JDK 25
- MySQL 8.0+
- Maven 3.6+
- Node.js 16+（前端部分）

### 2. 数据库初始化

```bash
# 执行 SQL 脚本
mysql -u root -p < src/main/resources/db/isi.sql
```

### 3. 配置文件

修改 `src/main/resources/application.yaml`：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/isi_db?useUnicode=true&characterEncoding=utf-8
    username: your_username
    password: your_password

# AI 配置（可选）
ai:
  enabled: true
  api-key: your-openai-api-key
  base-url: https://api.openai.com/v1
```

### 4. 运行项目

```bash
# 编译
mvn clean install

# 启动
mvn spring-boot:run
```

服务默认运行在：http://localhost:8081

### 5. 测试账号

- **管理员**: admin / admin123
- **HR**: hr_test / hr123
- **求职者**: user_test / user123

## API 接口说明

### 简历管理

```
POST   /resume/upload          # 上传简历
GET    /resume/list            # 获取我的简历列表
GET    /resume/{id}            # 获取简历详情
DELETE /resume/{id}            # 删除简历
GET    /resume/page            # 分页查询简历（管理员）
```

### 面试管理

```
POST   /interview/room/create         # 创建面试间
POST   /interview/room/{roomId}/start # 开始面试
POST   /interview/room/{roomId}/end   # 结束面试
POST   /interview/room/{roomId}/join  # HR 加入面试间
GET    /interview/my/list             # 获取我的面试列表
GET    /interview/room/{roomId}       # 获取面试详情
GET    /interview/room/code/{code}    # 通过房间号加入
```

### AI 面试问题与评估

```
POST   /ai/question/generate/{resumeId}     # 为简历生成 AI 面试题
POST   /ai/question/add                     # HR 手动添加问题
PUT    /ai/question/{id}                    # 更新问题
DELETE /ai/question/{id}                    # 删除问题
GET    /ai/question/list/{resumeId}         # 获取简历的所有问题
POST   /ai/evaluation/generate              # 生成面试评估（雷达图）
GET    /ai/evaluation/{roomId}              # 获取评估结果
PUT    /ai/evaluation/{id}                  # HR 修改评估分数
```

## WebSocket 实时通信

### 连接地址

```
/ws-interview
```

### 订阅主题

- `/topic/room/{roomId}` - 面试间广播消息
- `/queue/user/{userId}` - 个人消息队列

### 发送消息

- `/app/interview.message` - 发送面试消息

## 六边形雷达图数据说明

评估结果返回的 JSON 示例：

```json
{
  "technicalSkill": 85.0,      // 专业技能得分
  "communication": 78.0,        // 沟通能力得分
  "problemSolving": 82.0,       // 问题解决能力得分
  "learningAbility": 88.0,      // 学习能力得分
  "teamwork": 75.0,             // 团队协作能力得分
  "culturalFit": 90.0,          // 文化匹配度得分
  "totalScore": 83.0,           // 综合总分
  "radarData": [85, 78, 82, 88, 75, 90],  // 雷达图数组
  "recommendation": "RECOMMEND", // 推荐建议
  "strengths": "...",           // 优势分析
  "weaknesses": "..."           // 劣势分析
}
```

### 前端集成建议（ECharts）

```javascript
option = {
  radar: {
    indicator: [
      { name: '专业技能', max: 100 },
      { name: '沟通能力', max: 100 },
      { name: '问题解决', max: 100 },
      { name: '学习能力', max: 100 },
      { name: '团队协作', max: 100 },
      { name: '文化匹配', max: 100 }
    ]
  },
  series: [{
    type: 'radar',
    data: [{
      value: [85, 78, 82, 88, 75, 90],
      name: '面试评估'
    }]
  }]
};
```

## 开发计划

### 待完善功能

1. **简历解析**：集成 AI 实现 PDF/Word 简历的自动解析
2. **AI 评分**：对接大模型实现智能评分和反馈
3. **实时音视频**：集成 WebRTC 实现视频面试
4. **JWT 认证**：完整的登录认证体系
5. **前端页面**：Vue3/React 前端界面
6. **录像回放**：面试过程录制和回放

## 注意事项

1. 本项目采用 DDD 架构，请严格遵守领域驱动设计原则
2. Repository 层需要补充完整的实现（参考 scs 项目）
3. AI 相关功能需要配置有效的 API Key
4. WebSocket 在生产环境需要配置 CORS 和跨域
5. 文件上传路径需要根据实际环境调整

## 许可证

MIT License

## 联系方式

如有问题，请提交 Issue 或联系开发团队。
