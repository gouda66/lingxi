# 灵曦智能 (Lingxi Intelligence)

<p align="center">
  <img src="https://img.shields.io/badge/灵曦智能-AI%20Powered-blue?style=for-the-badge&logo=artificial-intelligence" alt="灵曦智能">
  <br>
  <b>一站式 AI 智能解决方案开源项目</b>
</p>

<p align="center">
  <a href="#项目简介"><img src="https://img.shields.io/badge/项目简介-了解详情-green"></a>
  <a href="#技术栈"><img src="https://img.shields.io/badge/技术栈-现代架构-blue"></a>
  <a href="#快速开始"><img src="https://img.shields.io/badge/快速开始-5分钟上手-orange"></a>
  <a href="#贡献指南"><img src="https://img.shields.io/badge/贡献指南-欢迎PR-purple"></a>
  <a href="#许可证"><img src="https://img.shields.io/badge/许可证-MIT-yellow"></a>
</p>

---

## 📋 目录

- [项目简介](#项目简介)
- [子项目概览](#子项目概览)
- [技术栈](#技术栈)
- [项目架构](#项目架构)
- [快速开始](#快速开始)
- [环境要求](#环境要求)
- [安装部署](#安装部署)
- [配置说明](#配置说明)
- [API 文档](#api-文档)
- [贡献指南](#贡献指南)
- [开发团队](#开发团队)
- [许可证](#许可证)
- [联系方式](#联系方式)

---

## 🎯 项目简介

**灵曦智能**是一个综合性的 AI 智能解决方案开源项目，旨在为企业提供智能化、自动化的业务处理能力。项目涵盖智能面试、智能外卖、SEO优化等多个业务场景，采用现代化的微服务架构和 AI Agent 技术，帮助企业降本增效。

### ✨ 核心特性

- 🤖 **AI 驱动** - 基于大语言模型的智能决策与交互
- 🏗️ **微服务架构** - 模块化设计，独立部署，易于扩展
- 🔌 **MCP 协议** - 标准化的 AI 服务通信协议
- 🌐 **全栈开源** - 前后端完整代码，开箱即用
- 📱 **多端适配** - 支持 Web、移动端等多种访问方式
- 🔐 **安全可靠** - 企业级安全设计与权限管理

---

## 📦 子项目概览

### 1. 灵曦智能面试系统 (ISI - Intelligent System Interview)

AI 驱动的智能面试平台，实现全流程自动化面试管理。

| 项目 | 技术栈 | 描述 |
|------|--------|------|
| [`isi-web`](./isi-web) | Vue3 / React + TypeScript | 智能面试前端界面，支持候选人端和面试官端 |
| [`isi`](./isi) | Java / Go / Node.js | 智能面试后端服务，处理业务逻辑与数据管理 |
| [`isi-agent`](./isi-agent) | Python + LLM | AI Agent 核心代码，实现智能问答与评估 |

**核心功能：**
- 🤖 AI 面试官自动发起面试
- 📝 智能简历解析与匹配
- 💬 多轮对话式面试流程
- 📊 自动化面试评估报告
- 📅 面试日程智能调度

---

### 2. 邮件服务 MCP (Mail MCP)

基于 MCP (Model Context Protocol) 协议的邮件发送服务，为 AI Agent 提供标准化的邮件能力。

| 项目 | 技术栈 | 描述 |
|------|--------|------|
| [`MailMcpServer`](./MailMcpServer) | Python / Node.js | MCP 服务端，提供邮件发送 API |
| [`MailMcpClient`](./MailMcpClient) | Python / TypeScript | MCP 客户端，供各业务系统调用 |

**核心功能：**
- 📧 支持 SMTP 邮件发送
- 📎 附件处理与模板渲染
- 🔒 邮件发送限流与重试
- 📈 发送状态追踪与统计

---

### 3. 灵曦智能外卖系统 (SCS - Smart Catering System)

AI 赋能的智能外卖平台，优化配送效率与用户体验。

| 项目 | 技术栈 | 描述 |
|------|--------|------|
| [`scs`](./scs) | Java / Go | 外卖系统后端，订单管理与配送调度 |
| [`scs-agent`](./scs-agent) | Python + MCP | 外卖 MCP 客户端，AI 订单处理与客服 |

**核心功能：**
- 🍔 智能餐厅推荐
- 🚴 配送路径优化
- 💬 AI 客服对话
- 📊 实时订单追踪
- 🔔 异常订单预警

---

### 4. 灵曦 SEO 优化系统 (SEO)

智能化的搜索引擎优化平台，提升网站排名与流量。

| 项目 | 技术栈 | 描述 |
|------|--------|------|
| [`seo`](./seo) | Python / Node.js | SEO 后端服务，关键词分析与优化建议 |
| [`seo-web`](./seo-web) | Vue3 / React | SEO 管理前端，数据可视化与报告展示 |

**核心功能：**
- 🔍 关键词智能挖掘
- 📈 排名监控与追踪
- 📝 内容优化建议
- 🔗 外链分析与建设
- 📊 竞品 SEO 分析

---

## 🛠️ 技术栈

### 前端技术栈

```
isi-web / seo-web
├── Vue 3 / React 18
├── TypeScript 5.x
├── Vite / Webpack
├── Element Plus / Ant Design
├── Pinia / Redux (状态管理)
├── Axios (HTTP 客户端)
└── ECharts / D3.js (数据可视化)
```

### 后端技术栈

```
isi / scs / seo
├── Java 17 / Spring Boot 3.x
├── Go 1.21 / Gin Framework
├── Node.js 18 / NestJS
├── PostgreSQL / MySQL (关系型数据库)
├── Redis (缓存与会话)
├── RabbitMQ / Kafka (消息队列)
├── Elasticsearch (搜索引擎)
└── Docker / Kubernetes (容器化)
```

### AI Agent 技术栈

```
isi-agent / scs-agent / MailMcpServer
├── Python 3.10+
├── LangChain / LlamaIndex (LLM 框架)
├── OpenAI API / Claude API / 文心一言
├── FastAPI (高性能 API 框架)
├── MCP SDK (Model Context Protocol)
├── ChromaDB / Milvus (向量数据库)
└── Celery (异步任务队列)
```

---

## 🏗️ 项目架构

```
灵曦智能 (Lingxi Intelligence)
│
├── 🎯 应用层 (Applications)
│   ├── isi-web        → 智能面试前端
│   ├── seo-web        → SEO 优化前端
│   └── (移动端 App)   → 外卖客户端
│
├── ⚙️ 服务层 (Services)
│   ├── isi            → 面试业务服务
│   ├── scs            → 外卖业务服务
│   └── seo            → SEO 业务服务
│
├── 🤖 AI 层 (AI Layer)
│   ├── isi-agent      → 面试 AI Agent
│   ├── scs-agent      → 外卖 AI Agent
│   ├── MailMcpServer  → 邮件 MCP 服务
│   └── MailMcpClient  → 邮件 MCP 客户端
│
└── 🔧 基础设施 (Infrastructure)
    ├── PostgreSQL     → 主数据库
    ├── Redis          → 缓存层
    ├── RabbitMQ       → 消息队列
    ├── Elasticsearch  → 搜索引擎
    └── MinIO / OSS    → 对象存储
```

### 数据流架构

```
用户请求 → API Gateway → 业务服务 → AI Agent → LLM API
                ↓              ↓         ↓
            认证授权       数据持久化   MCP 工具调用
                ↓              ↓         ↓
            JWT/OAuth2    PostgreSQL   外部服务
```

---

## 🚀 快速开始

### 5 分钟快速体验

```bash
# 1. 克隆项目仓库
git clone https://github.com/your-org/lingxi-intelligence.git
cd lingxi-intelligence

# 2. 启动基础服务 (Docker Compose)
docker-compose up -d postgres redis rabbitmq

# 3. 启动智能面试系统 (示例)
cd isi && ./mvnw spring-boot:run
cd ../isi-web && npm install && npm run dev
cd ../isi-agent && pip install -r requirements.txt && python main.py

# 4. 访问系统
# 前端: http://localhost:5173
# 后端 API: http://localhost:8080
# API 文档: http://localhost:8080/swagger-ui.html
```

---

## 📋 环境要求

### 基础环境

| 组件 | 最低版本 | 推荐版本 |
|------|----------|----------|
| Java | 17 | 21 LTS |
| Node.js | 18 | 20 LTS |
| Python | 3.10 | 3.11 |
| PostgreSQL | 14 | 16 |
| Redis | 6.2 | 7.2 |
| Docker | 20.10 | 24.x |
| Docker Compose | 2.0 | 2.20+ |

### 硬件要求

**开发环境：**
- CPU: 4 核+
- 内存: 8GB+
- 磁盘: 50GB SSD

**生产环境（最小配置）：**
- CPU: 8 核+
- 内存: 16GB+
- 磁盘: 200GB SSD
- GPU: NVIDIA T4 / A10 (AI 服务可选)

---

## 🔧 安装部署

### 方式一：Docker Compose 一键部署（推荐）

```bash
# 下载部署脚本
curl -fsSL https://raw.githubusercontent.com/your-org/lingxi-intelligence/main/deploy/install.sh | bash

# 或手动部署
docker-compose -f docker-compose.yml -f docker-compose.prod.yml up -d
```

### 方式二：Kubernetes 集群部署

```bash
# 部署到 K8s 集群
kubectl apply -f k8s/namespace.yaml
kubectl apply -f k8s/configmap.yaml
kubectl apply -f k8s/secrets.yaml
kubectl apply -f k8s/

# 查看部署状态
kubectl get pods -n lingxi
```

### 方式三：手动分步部署

详见各子项目的 README.md：
- [`isi/README.md`](./isi/README.md) - 面试系统部署指南
- [`scs/README.md`](./scs/README.md) - 外卖系统部署指南
- [`seo/README.md`](./seo/README.md) - SEO 系统部署指南

---

## ⚙️ 配置说明

### 环境变量配置

创建 `.env` 文件：

```env
# 数据库配置
DB_HOST=localhost
DB_PORT=5432
DB_NAME=lingxi
DB_USER=lingxi
DB_PASSWORD=your_secure_password

# Redis 配置
REDIS_HOST=localhost
REDIS_PORT=6379
REDIS_PASSWORD=

# AI 服务配置
OPENAI_API_KEY=sk-your-openai-key
CLAUDE_API_KEY=sk-your-anthropic-key
DEEPSEEK_API_KEY=sk-your-deepseek-key

# MCP 服务配置
MCP_SERVER_URL=http://localhost:3000
MCP_API_KEY=mcp-secret-key

# 邮件服务配置
SMTP_HOST=smtp.gmail.com
SMTP_PORT=587
SMTP_USER=your-email@gmail.com
SMTP_PASS=your-app-password

# JWT 配置
JWT_SECRET=your-jwt-secret-key-min-32-chars
JWT_EXPIRATION=86400

# 日志级别
LOG_LEVEL=INFO
```

### 配置文件结构

```
config/
├── application.yml          # 主配置
├── application-dev.yml      # 开发环境
├── application-prod.yml     # 生产环境
├── application-test.yml     # 测试环境
└── ai-config.yml            # AI 模型配置
```

---

## 📚 API 文档

### 在线文档

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **ReDoc**: http://localhost:8080/redoc
- **Postman Collection**: [下载](./docs/postman/lingxi-api-collection.json)

### 核心 API 概览

#### 智能面试 API

| 端点 | 方法 | 描述 |
|------|------|------|
| `/api/v1/interviews` | POST | 创建面试 |
| `/api/v1/interviews/{id}/start` | POST | 开始面试 |
| `/api/v1/interviews/{id}/answer` | POST | 提交答案 |
| `/api/v1/interviews/{id}/report` | GET | 获取评估报告 |

#### 外卖系统 API

| 端点 | 方法 | 描述 |
|------|------|------|
| `/api/v1/orders` | POST | 创建订单 |
| `/api/v1/orders/{id}/status` | GET | 查询订单状态 |
| `/api/v1/delivery/optimize` | POST | 优化配送路径 |

#### SEO 系统 API

| 端点 | 方法 | 描述 |
|------|------|------|
| `/api/v1/keywords/analyze` | POST | 关键词分析 |
| `/api/v1/rankings/track` | GET | 排名追踪 |
| `/api/v1/content/optimize` | POST | 内容优化建议 |

---

## 🤝 贡献指南

我们欢迎所有形式的贡献！

### 贡献流程

1. **Fork 项目** - 点击右上角 Fork 按钮
2. **克隆代码** - `git clone https://github.com/your-username/lingxi-intelligence.git`
3. **创建分支** - `git checkout -b feature/your-feature-name`
4. **提交更改** - `git commit -m "feat: add some feature"`
5. **推送分支** - `git push origin feature/your-feature-name`
6. **创建 PR** - 在 GitHub 上提交 Pull Request

### 代码规范

- **Java**: 遵循 [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html)
- **Python**: 遵循 [PEP 8](https://pep8.org/)，使用 Black 格式化
- **TypeScript**: 遵循 [Airbnb JavaScript Style Guide](https://github.com/airbnb/javascript)
- **Commit Message**: 遵循 [Conventional Commits](https://conventionalcommits.org/)

### 开发规范

```bash
# 代码提交前检查
./scripts/lint.sh
./scripts/test.sh

# 提交信息格式
type(scope): subject

# 示例
feat(isi): add AI interview evaluation algorithm
fix(scs): resolve order timeout issue
docs(readme): update deployment guide
```

### 问题反馈

- 🐛 [提交 Bug](https://github.com/your-org/lingxi-intelligence/issues/new?template=bug_report.md)
- 💡 [功能建议](https://github.com/your-org/lingxi-intelligence/issues/new?template=feature_request.md)
- ❓ [提问讨论](https://github.com/your-org/lingxi-intelligence/discussions)

---

## 👥 开发团队

### 核心团队

| 姓名 | 角色 | 负责模块 |
|------|------|----------|
| 张三 | 项目负责人 | 整体架构设计 |
| 李四 | 后端架构师 | isi / scs 后端 |
| 王五 | AI 工程师 | isi-agent / scs-agent |
| 赵六 | 前端负责人 | isi-web / seo-web |
| 孙七 | DevOps 工程师 | 部署与运维 |

### 贡献者

感谢所有为项目做出贡献的开发者！

<a href="https://github.com/your-org/lingxi-intelligence/graphs/contributors">
  <img src="https://contrib.rocks/image?repo=your-org/lingxi-intelligence" />
</a>

---

## 📄 许可证

本项目采用 [MIT 许可证](./LICENSE) 开源。

```
MIT License

Copyright (c) 2024 灵曦智能 (Lingxi Intelligence)

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND...
```

---

## 📞 联系方式

- 📧 **邮箱**: contact@lingxi-ai.com
- 🌐 **官网**: https://www.lingxi-ai.com
- 💬 **微信**: LingxiAI2024
- 🐦 **Twitter**: [@LingxiAI](https://twitter.com/LingxiAI)
- 📺 **Bilibili**: [灵曦智能](https://space.bilibili.com/lingxi-ai)

### 社区交流

- 💬 [Discord 社区](https://discord.gg/lingxi-ai)
- 📱 [微信群](https://www.lingxi-ai.com/wechat-group)
- 📝 [技术博客](https://blog.lingxi-ai.com)

---

## 🙏 致谢

感谢以下开源项目和技术：

- [Spring Boot](https://spring.io/projects/spring-boot) - Java 后端框架
- [Vue.js](https://vuejs.org/) - 前端框架
- [LangChain](https://langchain.com/) - LLM 应用框架
- [MCP](https://modelcontextprotocol.io/) - Model Context Protocol
- [PostgreSQL](https://www.postgresql.org/) - 开源数据库
- [Docker](https://www.docker.com/) - 容器化平台

---

<p align="center">
  <b>⭐ 如果这个项目对您有帮助，请给我们点个 Star！</b>
</p>

<p align="center">
  <a href="https://github.com/your-org/lingxi-intelligence/stargazers">
    <img src="https://img.shields.io/github/stars/your-org/lingxi-intelligence?style=social" alt="GitHub Stars">
  </a>
  <a href="https://github.com/your-org/lingxi-intelligence/network/members">
    <img src="https://img.shields.io/github/forks/your-org/lingxi-intelligence?style=social" alt="GitHub Forks">
  </a>
</p>

<p align="center">
  Made with ❤️ by 灵曦智能团队
</p>
