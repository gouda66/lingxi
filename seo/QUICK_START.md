# 快速开始 - Nginx 重写规则管理系统

## 🚀 5 分钟快速上手

### 1️⃣ 初始化数据库（2 分钟）

打开 MySQL 命令行或 Navicat，执行：

```sql
-- 使用现有数据库或创建新的
USE isi;

-- 运行 SQL 脚本
SOURCE D:/study_project/lingxi/seo/src/main/resources/db/seo.sql;
```

或者在命令行执行：

```bash
mysql -u root -p isi < D:/study_project/lingxi/seo/src/main/resources/db/seo.sql
```

### 2️⃣ 启动后端服务（1 分钟）

```bash
# 进入 seo 项目目录
cd D:/study_project/lingxi/seo

# Maven 构建
mvn clean install -DskipTests

# 启动服务（确保端口 8082 未被占用）
java -jar target/seo-0.0.1-SNAPSHOT.jar
```

看到以下日志表示启动成功：
```
Started SeoApplication in X.XXX seconds
Tomcat started on port(s): 8082 (http)
```

### 3️⃣ 启动前端服务（2 分钟）

打开新的命令行窗口：

```bash
# 进入前端项目目录
cd D:/study_project/lingxi/seo-web

# 安装依赖（首次运行需要）
yarn install

# 启动开发服务器
yarn dev
```

看到以下日志表示启动成功：
```
VITE v4.x.x  ready in xxx ms

➜  Local:   http://localhost:5173/
```

### 4️⃣ 访问系统

浏览器打开：**http://localhost:5173**

默认登录账号：
- **用户名**: `admin`
- **密码**: `admin123`

### 5️⃣ 添加第一条重写规则

1. 登录后，点击左侧菜单 **"Nginx 管理" → "重写规则"**
2. 点击 **"新增"** 按钮
3. 填写示例规则：

```
规则名称：测试重定向
原始模式：^/test/(.*)$
替换 URL：/hello/$1
标志：last
分类：GENERAL
描述：我的第一条重写规则
状态：启用
```

4. 点击 **"确定"** 保存

### 6️⃣ 生成 Nginx 配置

1. 在规则列表页面，点击 **"生成配置"** 按钮
2. 会看到生成的配置内容：

```nginx
# Nginx 重写规则配置
# 生成时间：2026-03-28 12:00:00

# 测试重定向 - 我的第一条重写规则
rewrite ^/test/(.*)$ /hello/$1 last;
```

3. 点击 **"复制配置"** 保存到剪贴板

---

## 📝 Ubuntu 服务器部署（可选）

如果你有 Ubuntu 服务器，按以下步骤部署：

### A. 上传并执行部署脚本

```bash
# 1. 将部署脚本上传到服务器
scp D:/study_project/lingxi/seo/deploy-nginx-config.sh user@your-server:/tmp/

# 2. SSH 登录服务器
ssh user@your-server

# 3. 执行部署
cd /tmp
chmod +x deploy-nginx-config.sh
sudo ./deploy-nginx-config.sh

# 4. 粘贴刚才复制的配置内容
# （右键或 Ctrl+Shift+V 粘贴）

# 5. 按 Ctrl+D 结束输入

# 6. 选择 y 重启 Nginx
是否立即重启 Nginx? (y/n): y
```

### B. 验证配置

```bash
# 查看配置文件
cat /etc/nginx/conf.d/rewrite.conf

# 测试 Nginx 配置
sudo nginx -t

# 重启 Nginx
sudo systemctl reload nginx

# 查看状态
sudo systemctl status nginx
```

---

## ✅ 完成！

现在你已经：
- ✅ 成功搭建了 Nginx 重写规则管理系统
- ✅ 添加了第一条重写规则
- ✅ 生成了 Nginx 配置文件
- ✅ （可选）部署到了 Ubuntu 服务器

## 🎯 下一步

你可以尝试：

1. **添加更多规则** - 参考 [NGINX_SEO_USAGE.md](./NGINX_SEO_USAGE.md) 中的示例
2. **配置 SEO 元数据** - 为不同页面设置独立的标题、关键词和描述
3. **导入现有规则** - 如果你已有 Nginx 配置，可以手动添加到系统中
4. **设置权限** - 为不同用户分配规则管理权限

---

## 🔧 常见问题排查

### 后端启动失败

**问题**: 端口 8082 已被占用  
**解决**: 
```bash
# Windows - 查找占用端口的进程
netstat -ano | findstr :8082
taskkill /PID <进程 ID> /F

# 修改端口号
编辑 seo/src/main/resources/application.yaml
server:
  port: 8083  # 改为其他端口
```

### 前端无法连接后端

**问题**: 跨域错误或 API 请求失败  
**解决**:
1. 检查后端是否在 8082 端口运行
2. 编辑 `seo-web/.env.development`:
   ```
   VUE_APP_BASE_API=http://localhost:8082
   ```
3. 重启前端：`yarn dev`

### 数据库连接失败

**问题**: 无法连接到 MySQL  
**解决**:
1. 确认 MySQL 服务已启动
2. 检查 `application.yaml` 中的数据库配置：
   ```yaml
   spring:
     datasource:
       url: jdbc:mysql://127.0.0.1:3306/isi?...
       username: root
       password: 你的密码
   ```

---

## 📚 更多资源

- **完整使用文档**: [NGINX_SEO_USAGE.md](./NGINX_SEO_USAGE.md)
- **配置示例**: [nginx-rewrite-example.conf](./nginx-rewrite-example.conf)
- **部署脚本**: [deploy-nginx-config.sh](./deploy-nginx-config.sh)

---

**祝你使用愉快！** 🎉

如有问题，请查看系统日志或联系技术支持。
