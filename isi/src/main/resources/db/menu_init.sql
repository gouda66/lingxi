use isi_db;

-- 菜单表结构
CREATE TABLE IF NOT EXISTS `sys_menu` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '菜单 ID',
  `menu_name` varchar(50) NOT NULL COMMENT '菜单名称',
  `parent_id` bigint(20) DEFAULT 0 COMMENT '父菜单 ID',
  `order_num` int(11) DEFAULT 0 COMMENT '显示顺序',
  `path` varchar(200) DEFAULT '' COMMENT '路由地址',
  `component` varchar(255) DEFAULT NULL COMMENT '组件路径',
  `is_frame` int(11) DEFAULT 1 COMMENT '是否为外链（0 是 1 否）',
  `is_cache` int(11) DEFAULT 0 COMMENT '是否缓存（0 缓存 1 不缓存）',
  `menu_type` char(1) DEFAULT '' COMMENT '菜单类型（M 目录 C 菜单 F 按钮）',
  `visible` char(1) DEFAULT 0 COMMENT '显示状态（0 显示 1 隐藏）',
  `icon` varchar(100) DEFAULT '#' COMMENT '菜单图标',
  `perms` varchar(100) DEFAULT NULL COMMENT '权限标识',
  `remark` varchar(500) DEFAULT '' COMMENT '备注',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='菜单权限表';

-- 用户角色关联表
CREATE TABLE IF NOT EXISTS `sys_user_role` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键 ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户 ID',
  `role_id` bigint(20) NOT NULL COMMENT '角色 ID',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';

-- 角色菜单关联表
CREATE TABLE IF NOT EXISTS `sys_role_menu` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键 ID',
  `role_id` bigint(20) NOT NULL COMMENT '角色 ID',
  `menu_id` bigint(20) NOT NULL COMMENT '菜单 ID',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色菜单关联表';

-- 初始化菜单数据
INSERT INTO `sys_menu` (`id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `is_frame`, `is_cache`, `menu_type`, `visible`, `icon`, `perms`, `remark`, `create_time`, `update_time`) VALUES 
(1, '系统管理', 0, 1, 'system', NULL, 1, 0, 'M', 0, 'system', 'system', '系统管理目录', '2026-03-17 00:00:00', '2026-03-17 00:00:00'),
(2, '用户管理', 1, 1, 'user', 'system/user/index', 1, 0, 'C', 0, 'user', 'system:user:list', '用户管理目录', '2026-03-17 00:00:00', '2026-03-17 00:00:00'),
(3, '角色管理', 1, 2, 'role', 'system/role/index', 1, 0, 'C', 0, 'peoples', 'system:role:list', '角色管理目录', '2026-03-17 00:00:00', '2026-03-17 00:00:00'),
(4, '菜单管理', 1, 3, 'menu', 'system/menu/index', 1, 0, 'C', 0, 'tree-table', 'system:menu:list', '菜单管理目录', '2026-03-17 00:00:00', '2026-03-17 00:00:00'),
(5, '部门管理', 1, 4, 'dept', 'system/dept/index', 1, 0, 'C', 0, 'tree', 'system:dept:list', '部门管理目录', '2026-03-17 00:00:00', '2026-03-17 00:00:00'),
(6, '岗位管理', 1, 5, 'post', 'system/post/index', 1, 0, 'C', 0, 'post', 'system:post:list', '岗位管理目录', '2026-03-17 00:00:00', '2026-03-17 00:00:00'),
(7, '字典管理', 1, 6, 'dict', 'system/dict/index', 1, 0, 'C', 0, 'dict', 'system:dict:list', '字典管理目录', '2026-03-17 00:00:00', '2026-03-17 00:00:00'),
(8, '参数设置', 1, 7, 'config', 'system/config/index', 1, 0, 'C', 0, 'edit', 'system:config:list', '参数设置目录', '2026-03-17 00:00:00', '2026-03-17 00:00:00'),
(9, '通知公告', 1, 8, 'notice', 'system/notice/index', 1, 0, 'C', 0, 'message', 'system:notice:list', '通知公告目录', '2026-03-17 00:00:00', '2026-03-17 00:00:00'),
(10, '日志管理', 1, 9, 'log', NULL, 1, 0, 'M', 0, 'log', '', '日志管理目录', '2026-03-17 00:00:00', '2026-03-17 00:00:00'),
(11, '操作日志', 10, 1, 'operlog', 'monitor/operlog/index', 1, 0, 'C', 0, 'form', 'monitor:operlog:list', '操作日志目录', '2026-03-17 00:00:00', '2026-03-17 00:00:00'),
(12, '登录日志', 10, 2, 'logininfor', 'monitor/logininfor/index', 1, 0, 'C', 0, 'login', 'monitor:logininfor:list', '登录日志目录', '2026-03-17 00:00:00', '2026-03-17 00:00:00');

-- 初始化用户角色关联 (假设用户 ID=1 是管理员，角色 ID=1 是管理员角色)
INSERT INTO `sys_user_role` (`id`, `user_id`, `role_id`, `create_time`) VALUES (1, 1, 1, '2026-03-17 00:00:00');

-- 初始化角色菜单关联 (管理员拥有所有菜单权限)
INSERT INTO `sys_role_menu` (`id`, `role_id`, `menu_id`, `create_time`) VALUES 
(1, 1, 1, '2026-03-17 00:00:00'), (2, 1, 2, '2026-03-17 00:00:00'), (3, 1, 3, '2026-03-17 00:00:00'), (4, 1, 4, '2026-03-17 00:00:00'),
(5, 1, 5, '2026-03-17 00:00:00'), (6, 1, 6, '2026-03-17 00:00:00'), (7, 1, 7, '2026-03-17 00:00:00'), (8, 1, 8, '2026-03-17 00:00:00'),
(9, 1, 9, '2026-03-17 00:00:00'), (10, 1, 10, '2026-03-17 00:00:00'), (11, 1, 11, '2026-03-17 00:00:00'), (12, 1, 12, '2026-03-17 00:00:00');
