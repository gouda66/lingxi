use isi_db;

-- 初始化菜单数据
INSERT IGNORE INTO `sys_menu` (`id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `is_frame`, `is_cache`, `menu_type`, `hidden`, `icon`, `perms`, `remark`, `create_time`, `update_time`) VALUES
(1, '系统管理', 0, 1, '/system', NULL, 1, 0, 'M', 0, 'system', 'system', '系统管理目录', '2026-03-17 00:00:00', '2026-03-17 00:00:00'),
(2, '用户管理', 1, 1, 'user', 'system/user/index', 1, 0, 'C', 0, 'user', 'system:user:list', '用户管理页面', '2026-03-17 00:00:00', '2026-03-17 00:00:00'),
(6, '岗位管理', 1, 2, 'post', 'system/post/index', 1, 0, 'C', 0, 'post', 'system:post:list', '岗位管理页面', '2026-03-17 00:00:00', '2026-03-17 00:00:00'),
(7, 'HR 管理', 1, 3, 'hr', 'system/hr/index', 1, 0, 'C', 0, 'post', 'system:hr:list', 'HR 管理页面', '2026-03-17 00:00:00', '2026-03-17 00:00:00'),
(3, '面试管理', 0, 2, '/interview', NULL, 1, 0, 'M', 0, 'edit', 'interview', '面试管理目录', '2026-03-17 00:00:00', '2026-03-17 00:00:00'),
(4, '面试列表', 3, 1, 'list', 'interview/room/index', 1, 0, 'C', 0, 'list', 'interview:list', '面试列表页面', '2026-03-17 00:00:00', '2026-03-17 00:00:00'),
(5, '简历列表', 3, 2, 'resume', 'interview/resume/index', 1, 0, 'C', 0, 'form', 'interview:resume:list', '简历列表页面', '2026-03-17 00:00:00', '2026-03-17 00:00:00');

-- 初始化用户角色关联 (假设用户 ID=1 是管理员，角色 ID=1 是管理员角色)
INSERT IGNORE INTO `sys_user_role` (`id`, `user_id`, `role_id`, `create_time`) VALUES (1, 1, 1, '2026-03-17 00:00:00');

-- 初始化角色菜单关联 (管理员拥有所有菜单权限)
INSERT IGNORE INTO `sys_role_menu` (`id`, `role_id`, `menu_id`, `create_time`) VALUES 
(1, 1, 1, '2026-03-17 00:00:00'), (2, 1, 2, '2026-03-17 00:00:00'), (3, 1, 3, '2026-03-17 00:00:00'), (4, 1, 4, '2026-03-17 00:00:00'),
(5, 1, 5, '2026-03-17 00:00:00'), (6, 1, 6, '2026-03-17 00:00:00'), (7, 1, 7, '2026-03-17 00:00:00');
