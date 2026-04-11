# AI面试Agent Docker 部署指南

## 📋 前置要求

- Docker >= 20.10
- Docker Compose >= 2.0
- 服务器内存建议 >= 2GB

## 🚀 快速部署

### 方式一：使用部署脚本（推荐）

```bash
# 1. 上传项目文件到服务器
scp -r isi-agent user@your-server:/path/to/deploy

# 2. 进入项目目录
cd /path/to/deploy/isi-agent

# 3. 赋予执行权限
chmod +x deploy.sh

# 4. 执行部署
./deploy.sh
```

### 方式二：手动部署

```bash
# 1. 构建镜像
docker-compose build

# 2. 启动服务
docker-compose up -d

# 3. 查看日志
docker-compose logs -f
```

## 🔧 配置说明

### 端口映射

默认将容器的 8000 端口映射到宿主机的 8000 端口。如需修改，编辑 `docker-compose.yml`：

```yaml
ports:
  - "8080:8000"  # 宿主机端口:容器端口
```

### 资源限制

在 `docker-compose.yml` 中已配置资源限制，可根据服务器配置调整：

```yaml
deploy:
  resources:
    limits:
      cpus: '2'      # CPU核心数
      memory: 2G     # 最大内存
```

### 环境变量

如需配置 API Key 等敏感信息，可以：

1. 创建 `.env` 文件：
```bash
OPENAI_API_KEY=your-api-key
```

2. 在 `docker-compose.yml` 中添加：
```yaml
environment:
  - OPENAI_API_KEY=${OPENAI_API_KEY}
```

## 📊 管理命令

```bash
# 查看服务状态
docker-compose ps

# 查看实时日志
docker-compose logs -f

# 停止服务
docker-compose down

# 重启服务
docker-compose restart

# 更新代码后重新部署
docker-compose up -d --build

# 进入容器内部
docker exec -it isi-agent bash

# 查看容器资源使用情况
docker stats isi-agent
```

## 🔍 健康检查

服务启动后，可以通过以下方式检查：

```bash
# 浏览器访问
http://服务器IP:8000/docs

# 或使用 curl
curl http://localhost:8000/docs
```

## 🐛 故障排查

### 1. 容器无法启动

```bash
# 查看详细日志
docker-compose logs isi-agent

# 检查端口占用
netstat -tlnp | grep 8000
```

### 2. 内存不足

调整 `docker-compose.yml` 中的内存限制：

```yaml
deploy:
  resources:
    limits:
      memory: 4G  # 增加内存限制
```

### 3. 重建容器

```bash
# 完全清理并重建
docker-compose down -v
docker-compose build --no-cache
docker-compose up -d
```

## 🔒 安全建议

1. **不要将 API Key 硬编码在代码中**，使用环境变量或密钥管理服务
2. 生产环境建议使用 Nginx 反向代理并配置 HTTPS
3. 定期更新基础镜像以获取安全补丁
4. 配置防火墙规则，只开放必要端口

## 📝 Nginx 反向代理配置（可选）

如果需要域名访问和 HTTPS，可以配置 Nginx：

```nginx
server {
    listen 80;
    server_name your-domain.com;

    location / {
        proxy_pass http://localhost:8000;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

## 🔄 自动更新（可选）

可以使用 Watchtower 实现自动更新：

```yaml
# 添加到 docker-compose.yml
services:
  watchtower:
    image: containrrr/watchtower
    volumes:
      - /var/run/docker.sock:/var/run/docker.sock
    command: isi-agent --interval 3600
```

## 📞 技术支持

如有问题，请检查：
1. Docker 版本是否符合要求
2. 服务器资源是否充足
3. 网络连接是否正常
4. 日志中的错误信息

---

**祝部署顺利！** 🎉
