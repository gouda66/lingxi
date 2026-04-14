@echo off
chcp 65001 >nul
echo ======================================
echo   AI面试Agent - Docker 部署脚本
echo ======================================
echo.

REM 检查 Docker 是否安装
where docker >nul 2>nul
if %errorlevel% neq 0 (
    echo ❌ Docker 未安装，请先安装 Docker Desktop
    pause
    exit /b 1
)

REM 检查 Docker Compose 是否安装
docker compose version >nul 2>nul
if %errorlevel% neq 0 (
    echo ❌ Docker Compose 未安装
    pause
    exit /b 1
)

echo ✅ Docker 环境检查通过
echo.

REM 创建日志目录
if not exist logs mkdir logs

REM 构建并启动服务
echo 🔨 正在构建 Docker 镜像...
docker compose build --no-cache

echo.
echo 🚀 正在启动服务...
docker compose up -d

REM 等待服务启动
echo.
echo ⏳ 等待服务启动...
timeout /t 5 /nobreak >nul

REM 检查服务状态
echo.
echo 📊 服务状态：
docker compose ps

echo.
echo ======================================
echo   ✅ 部署完成！
echo ======================================
echo.
echo 📝 访问地址：
echo    - API文档: http://localhost:8000/docs
echo.
echo 🔧 常用命令：
echo    - 查看日志: docker compose logs -f
echo    - 停止服务: docker compose down
echo    - 重启服务: docker compose restart
echo    - 更新服务: docker compose up -d --build
echo.
pause
