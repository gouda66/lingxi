#!/bin/bash

# Nginx 重写规则配置自动部署脚本
# 使用方法：sudo ./deploy-nginx-config.sh [配置文件路径]

set -e

CONFIG_FILE="${1:-/etc/nginx/conf.d/rewrite.conf}"
BACKUP_DIR="/var/backups/nginx"
TIMESTAMP=$(date +%Y%m%d_%H%M%S)

echo "========================================="
echo "Nginx 重写规则配置部署脚本"
echo "========================================="
echo ""

# 检查是否以 root 运行
if [ "$EUID" -ne 0 ]; then 
  echo "请使用 sudo 运行此脚本"
  exit 1
fi

# 创建备份目录
mkdir -p "$BACKUP_DIR"

# 备份当前配置
if [ -f "$CONFIG_FILE" ]; then
  echo "正在备份当前配置..."
  cp "$CONFIG_FILE" "$BACKUP_DIR/rewrite.conf.$TIMESTAMP.bak"
  echo "✓ 已备份到：$BACKUP_DIR/rewrite.conf.$TIMESTAMP.bak"
else
  echo "ℹ 配置文件不存在，跳过备份"
fi

echo ""
echo "请粘贴 Nginx 配置内容 (按 Ctrl+D 结束输入):"
echo "----------------------------------------"

# 读取输入并写入配置文件
cat > "$CONFIG_FILE"

echo "----------------------------------------"
echo "✓ 配置已写入：$CONFIG_FILE"
echo ""

# 测试 Nginx 配置
echo "正在测试 Nginx 配置..."
if nginx -t; then
  echo "✓ Nginx 配置测试通过"
  echo ""
  
  # 询问是否重启 Nginx
  read -p "是否立即重启 Nginx? (y/n): " -n 1 -r
  echo
  if [[ $REPLY =~ ^[Yy]$ ]]; then
    echo "正在重启 Nginx..."
    systemctl reload nginx
    echo "✓ Nginx 已重启"
  else
    echo "⚠ 请手动重启 Nginx: sudo systemctl reload nginx"
  fi
else
  echo "✗ Nginx 配置测试失败！"
  echo "已恢复到备份配置"
  cp "$BACKUP_DIR/rewrite.conf.$TIMESTAMP.bak" "$CONFIG_FILE"
  exit 1
fi

echo ""
echo "========================================="
echo "部署完成！"
echo "========================================="
