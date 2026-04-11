#!/bin/bash

# AI面试Agent Docker 部署脚本

echo "======================================"
echo "  AI面试Agent - Docker 部署脚本"
echo "======================================"

# 检查 Docker 是否安装
if ! command -v docker &> /dev/null; then
    echo "❌ Docker 未安装，请先安装 Docker"
    exit 1
fi

# 检查 Docker Compose 是否安装
if ! command -v docker-compose &> /dev/null && ! docker compose version &> /dev/null; then
    echo "❌ Docker Compose 未安装，请先安装 Docker Compose"
    exit 1
fi

echo "✅ Docker 环境检查通过"

# 确定使用的 docker-compose 命令
if docker compose version &> /dev/null; then
    DOCKER_COMPOSE="docker compose"
else
    DOCKER_COMPOSE="docker-compose"
fi

# 创建日志目录
mkdir -p logs

# 构建并启动服务
echo ""
echo "🔨 正在构建 Docker 镜像..."
$DOCKER_COMPOSE build --no-cache

echo ""
echo "🚀 正在启动服务..."
$DOCKER_COMPOSE up -d

# 等待服务启动
echo ""
echo "⏳ 等待服务启动..."
sleep 5

# 检查服务状态
echo ""
echo "📊 服务状态："
$DOCKER_COMPOSE ps

echo ""
echo "======================================"
echo "  ✅ 部署完成！"
echo "======================================"
echo ""
echo "📝 访问地址："
echo "   - API文档: http://服务器IP:8000/docs"
echo "   - 健康检查: http://服务器IP:8000/health"
echo ""
echo "🔧 常用命令："
echo "   - 查看日志: $DOCKER_COMPOSE logs -f"
echo "   - 停止服务: $DOCKER_COMPOSE down"
echo "   - 重启服务: $DOCKER_COMPOSE restart"
echo "   - 更新服务: $DOCKER_COMPOSE up -d --build"
echo ""
