# Nginx 重写规则同步功能使用说明

## 功能概述

本系统支持将数据库中的 Nginx 重写规则同步到 Ubuntu 服务器，并自动重新加载 Nginx 配置。

## 配置步骤

### 1. 配置 SSH 连接信息

在 `seo/src/main/resources/application.yaml` 中配置 Ubuntu 服务器信息：

```yaml
nginx:
  ssh:
    host: 你的 Ubuntu 服务器 IP
    port: 22  # SSH 端口，默认 22
    username: root  # SSH 用户名
    password: your_password  # SSH 密码
  config:
    path: /etc/nginx/conf.d  # Nginx 配置文件路径
```

### 2. Ubuntu 服务器准备

确保 Ubuntu 服务器满足以下条件：

1. **安装 Nginx**
   ```bash
   sudo apt update
   sudo apt install nginx
   ```

2. **配置 sudo 权限**（允许 nginx 用户无需密码执行 reload 命令）
   
   编辑 sudoers 文件：
   ```bash
   sudo visudo
   ```
   
   添加以下行：
   ```
   www-data ALL=(ALL) NOPASSWD: /usr/bin/systemctl reload nginx
   ```

3. **确保 Nginx 配置目录存在**
   ```bash
   sudo mkdir -p /etc/nginx/conf.d
   ```

### 3. 使用步骤

1. **启动后端服务**
   ```bash
   cd seo
   mvn clean install
   java -jar target/seo-0.0.1-SNAPSHOT.jar
   ```

2. **启动前端服务**
   ```bash
   cd seo-web
   npm install
   npm run dev
   ```

3. **访问重写规则管理页面**
   - 打开浏览器访问：http://localhost:5170（或你的前端端口）
   - 进入 Nginx 管理 -> 重写规则

4. **添加重写规则**
   - 点击"新增"按钮
   - 填写规则信息
   - 点击"确定"保存

5. **同步配置到 Nginx**
   - 点击"同步配置"按钮
   - 确认操作
   - 等待同步完成

### 4. 同步流程说明

系统会自动执行以下步骤：

1. ✅ 测试 SSH 连接到 Ubuntu 服务器
2. ✅ 查询所有启用的重写规则
3. ✅ 生成 Nginx 配置文件内容
4. ✅ 通过 SFTP 上传到 `/etc/nginx/conf.d/rewrite.conf`
5. ✅ 执行 `nginx -t` 测试配置语法
6. ✅ 执行 `sudo systemctl reload nginx` 重新加载配置

### 5. 示例规则

#### SEO 优化 - 永久重定向
```
规则名称：博客文章永久重定向
分类：SEO
原始模式：^/blog/(.*)$
替换 URL：/articles/$1
标志：permanent
排序：10
状态：启用
```

#### API 伪静态化
```
规则名称：产品详情页伪静态
分类：API
原始模式：^/product/([0-9]+)\.html$
替换 URL：/api/product/detail?id=$1
标志：last
排序：5
状态：启用
```

#### URL 美化
```
规则名称：分类页面 URL 美化
分类：GENERAL
原始模式：^/category/([a-z0-9-]+)/?$
替换 URL：/products?category=$1
标志：last
排序：8
状态：启用
```

### 6. 故障排查

#### SSH 连接失败
- 检查服务器 IP、端口、用户名、密码是否正确
- 确认服务器防火墙允许 SSH 连接（端口 22）
- 测试 SSH 连接：`ssh username@host -p port`

#### 权限不足
- 确保配置了 sudo 免密码执行 nginx reload 命令
- 检查 Nginx 配置目录的写权限

#### 配置测试失败
- 查看生成的配置文件内容
- 检查正则表达式语法是否正确
- 查看 Nginx 错误日志：`sudo tail -f /var/log/nginx/error.log`

### 7. 安全建议

1. **使用 SSH 密钥认证**（推荐）
   - 修改代码使用 SSH 密钥代替密码
   - 提高安全性

2. **限制 IP 访问**
   - 在防火墙中限制只有应用服务器 IP 可以访问 SSH 端口

3. **定期备份**
   - 定期备份 Nginx 配置文件
   - 可以在系统中添加配置历史功能

### 8. 注意事项

⚠️ **重要提示**：
- 同步操作会覆盖 `/etc/nginx/conf.d/rewrite.conf` 文件
- 确保没有其他重要的自定义配置在该文件中
- 建议先在测试环境验证后再在生产环境使用
- 同步前建议先"生成配置"预览配置内容

### 9. 依赖说明

系统使用以下依赖：
- **JSch** (0.1.55) - Java SSH 库
- **Spring Boot** (3.5.13)
- **MyBatis-Plus** (3.5.9)
- **Hutool** (5.8.32)

### 10. 技术实现

后端实现：
- `SshUtil.java` - SSH 工具类，处理文件上传和命令执行
- `NginxRewriteRuleServiceImpl.java` - 同步逻辑实现
- `NginxRewriteRuleController.java` - REST API 接口

前端实现：
- `seo-web/src/views/nginx/rewrite/index.vue` - 管理页面
- `seo-web/src/api/nginx/rewrite.js` - API 调用
