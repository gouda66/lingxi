# Nginx 请求重写与 SEO 优化系统 - 使用指南

## 📋 目录

- [功能概述](#功能概述)
- [技术架构](#技术架构)
- [快速开始](#快速开始)
- [使用教程](#使用教程)
- [部署指南](#部署指南)
- [常见问题](#常见问题)

---

## 功能概述

本系统提供可视化的 Nginx 重写规则管理界面，让您能够：

✅ **可视化配置** - 通过 Web 界面轻松添加、编辑、删除 URL 重写规则  
✅ **分类管理** - 支持通用、SEO、API 三种规则分类  
✅ **智能生成** - 自动生成标准 Nginx 配置文件  
✅ **版本控制** - 保存配置历史，支持回滚  
✅ **SEO 优化** - 为不同页面配置独立的 SEO 元数据  

### 典型应用场景

1. **URL 规范化** - 将旧 URL 重定向到新 URL，保持 SEO 权重
2. **伪静态化** - 将动态 URL 转换为搜索引擎友好的静态格式
3. **多语言支持** - 根据路径自动切换到对应语言版本
4. **API 网关** - 统一 API 入口，简化前端调用

---

## 技术架构

### 后端技术栈
- **框架**: Spring Boot 3.x
- **ORM**: MyBatis Plus
- **数据库**: MySQL 8.0+
- **权限**: Spring Security + JWT

### 前端技术栈
- **框架**: Vue 3 + Vite
- **UI 库**: Element Plus
- **状态管理**: Pinia
- **路由**: Vue Router

### 服务器
- **Web 服务器**: Nginx (Ubuntu/Debian)
- **运行环境**: JDK 17+, Node.js 18+

---

## 快速开始

### 1. 数据库初始化

```bash
# 登录 MySQL
mysql -u root -p

# 创建数据库（如果还没有）
CREATE DATABASE isi CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

# 导入表结构
USE isi;
SOURCE /path/to/seo/src/main/resources/db/seo.sql;
```

### 2. 启动后端服务

```bash
cd seo
mvn clean install
java -jar target/seo-0.0.1-SNAPSHOT.jar
```

后端服务将在 `http://localhost:8082` 启动

### 3. 启动前端服务

```bash
cd seo-web
yarn install
yarn dev
```

前端服务将在 `http://localhost:5173` 启动

### 4. 登录系统

使用默认账号登录：
- 用户名：`admin`
- 密码：`admin123`

---

## 使用教程

### 第一步：添加重写规则

1. 登录后进入 **"Nginx 管理" → "重写规则"**
2. 点击 **"新增"** 按钮
3. 填写规则信息：

#### 示例 1：旧博客文章重定向

```
规则名称：博客文章永久重定向
原始模式：^/blog/(.*)$
替换 URL：/articles/$1
标志：permanent
分类：SEO
描述：将旧博客路径永久重定向到文章页面
```

#### 示例 2：产品页面伪静态

```
规则名称：产品详情页伪静态
原始模式：^/product/([0-9]+).html$
替换 URL：/api/product/detail?id=$1
标志：last
分类：API
描述：将静态 HTML 请求转发到产品详情 API
```

#### 示例 3：多语言切换

```
规则名称：英文版本切换
原始模式：^/en/(.*)$
替换 URL：/$1?lang=en
标志：last
分类：GENERAL
描述：访问英文版本页面
```

4. 点击 **"确定"** 保存规则

### 第二步：调整规则顺序

规则的匹配顺序很重要！系统会按照 **排序值从大到小** 的顺序进行匹配。

- 更具体的规则应该设置更大的排序值
- 通用规则设置较小的排序值

### 第三步：生成 Nginx 配置

1. 在规则列表页面，点击 **"生成配置"** 按钮
2. 系统会生成类似以下的配置：

```nginx
# Nginx 重写规则配置
# 生成时间：2026-03-28 10:30:45

# 博客文章永久重定向 - 将旧博客路径永久重定向到文章页面
rewrite ^/blog/(.*)$ /articles/$1 permanent;

# 产品详情页伪静态 - 将静态 HTML 请求转发到产品详情 API
rewrite ^/product/([0-9]+).html$ /api/product/detail?id=$1 last;

# 英文版本切换 - 访问英文版本页面
rewrite ^/en/(.*)$ /$1?lang=en last;
```

3. 点击 **"复制配置"** 按钮

### 第四步：部署到 Ubuntu 服务器

#### 方法一：使用部署脚本（推荐）

```bash
# 1. 上传脚本到服务器
scp deploy-nginx-config.sh user@your-server:/tmp/

# 2. SSH 登录服务器
ssh user@your-server

# 3. 执行部署脚本
cd /tmp
chmod +x deploy-nginx-config.sh
sudo ./deploy-nginx-config.sh

# 4. 粘贴配置内容（从管理系统复制的）
# 右键粘贴或 Ctrl+Shift+V

# 5. 按 Ctrl+D 结束输入

# 6. 选择是否重启 Nginx
是否立即重启 Nginx? (y/n): y
```

#### 方法二：手动部署

```bash
# 1. 创建配置文件
sudo vim /etc/nginx/conf.d/rewrite.conf

# 2. 粘贴配置内容，保存退出

# 3. 测试配置
sudo nginx -t

# 4. 重启 Nginx
sudo systemctl reload nginx
```

### 第五步：验证配置

```bash
# 测试重写规则是否生效
curl -I http://your-domain.com/blog/old-article
# 应该返回 301 重定向到 /articles/old-article

# 查看 Nginx 错误日志
sudo tail -f /var/log/nginx/error.log
```

---

## 部署指南

### Ubuntu 服务器完整部署流程

#### 1. 安装必要软件

```bash
# 更新包索引
sudo apt update

# 安装 Nginx
sudo apt install -y nginx

# 安装 JDK 17
sudo apt install -y openjdk-17-jdk

# 安装 Node.js 18
curl -fsSL https://deb.nodesource.com/setup_18.x | sudo -E bash -
sudo apt install -y nodejs

# 安装 MySQL（如果需要）
sudo apt install -y mysql-server
```

#### 2. 配置 Nginx

```bash
# 创建站点配置
sudo vim /etc/nginx/sites-available/myapp

server {
    listen 80;
    server_name your-domain.com;
    
    # 包含重写规则
    include /etc/nginx/conf.d/rewrite.conf;
    
    # 前端静态文件
    location / {
        root /var/www/myapp;
        try_files $uri $uri/ /index.html;
    }
    
    # 反向代理到后端
    location /seo/ {
        proxy_pass http://localhost:8082/seo/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    }
}

# 启用站点
sudo ln -s /etc/nginx/sites-available/myapp /etc/nginx/sites-enabled/

# 删除默认站点
sudo rm /etc/nginx/sites-enabled/default

# 测试并重启
sudo nginx -t
sudo systemctl restart nginx
```

#### 3. 部署后端服务

```bash
# 构建项目
cd /path/to/lingxi/seo
mvn clean package -DskipTests

# 创建 systemd 服务
sudo vim /etc/systemd/system/seo.service

[Unit]
Description=SEO Backend Service
After=syslog.target network.target

[Service]
User=www-data
ExecStart=/usr/bin/java -jar /path/to/seo/target/seo-0.0.1-SNAPSHOT.jar
SuccessExitStatus=143
Restart=on-failure

[Install]
WantedBy=multi-user.target

# 启动服务
sudo systemctl daemon-reload
sudo systemctl enable seo
sudo systemctl start seo
sudo systemctl status seo
```

#### 4. 部署前端应用

```bash
# 构建前端
cd /path/to/lingxi/seo-web
yarn install
yarn build

# 复制到 Nginx 目录
sudo cp -r dist/* /var/www/myapp/

# 设置权限
sudo chown -R www-data:www-data /var/www/myapp
```

---

## 常见问题

### Q1: 规则不生效怎么办？

**检查清单：**
1. ✅ 确认规则已启用（状态开关为绿色）
2. ✅ 确认 Nginx 已重启：`sudo systemctl reload nginx`
3. ✅ 检查 Nginx 错误日志：`sudo tail -f /var/log/nginx/error.log`
4. ✅ 确认 rewrite.conf 被正确包含在主配置中

### Q2: 如何调试重写规则？

```bash
# 开启 Nginx 重写日志
sudo vim /etc/nginx/nginx.conf

# 在 http 块中添加：
rewrite_log on;

# 重启 Nginx
sudo systemctl reload nginx

# 查看日志
sudo tail -f /var/log/nginx/access.log | grep rewrite
```

### Q3: 正则表达式怎么写？

**常用示例：**

| 需求 | 原始模式 | 替换 URL |
|------|---------|---------|
| 匹配所有二级路径 | `^/news/(.*)$` | `/articles/$1` |
| 匹配数字 ID | `^/user/([0-9]+).html$` | `/user/profile?id=$1` |
| 匹配字母分类 | `^/category/([a-z]+)/$` | `/products?cat=$1` |
| 精确匹配 | `^/about-us/?$` | `/page/about` |

### Q4: 各个标志 (flag) 的区别？

- **last**: 完成本次匹配后停止，重新处理新的 URI
- **break**: 终止匹配，使用当前替换后的 URI
- **redirect**: 临时重定向 (302)，浏览器地址栏改变
- **permanent**: 永久重定向 (301)，有利于 SEO

### Q5: 如何备份和恢复配置？

```bash
# 备份
sudo cp /etc/nginx/conf.d/rewrite.conf /var/backups/nginx/rewrite.conf.backup

# 恢复
sudo cp /var/backups/nginx/rewrite.conf.backup /etc/nginx/conf.d/rewrite.conf
sudo systemctl reload nginx
```

---

## 技术支持

如有问题，请查看：
- 后端日志：`seo/logs/app.log`
- Nginx 日志：`/var/log/nginx/error.log`
- 数据库连接：确保 MySQL 服务正常运行

---

**祝您使用愉快！** 🎉
