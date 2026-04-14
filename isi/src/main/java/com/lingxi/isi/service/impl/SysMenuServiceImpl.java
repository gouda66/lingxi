package com.lingxi.isi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lingxi.isi.common.result.R;
import com.lingxi.isi.mapper.SysMenuMapper;
import com.lingxi.isi.models.dto.MenuDTO;
import com.lingxi.isi.models.entity.SysMenu;
import com.lingxi.isi.models.request.MenuRequest;
import com.lingxi.isi.service.ISysMenuService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 菜单服务实现类
 */
@Slf4j
@Service
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements ISysMenuService {

    private final SysMenuMapper sysMenuMapper;

    public SysMenuServiceImpl(SysMenuMapper sysMenuMapper) {
        this.sysMenuMapper = sysMenuMapper;
    }

    @Override
    public R<Map<String, Object>> treeselect() {
        // 查询所有正常状态的菜单
        LambdaQueryWrapper<SysMenu> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysMenu::getStatus, "0");
        queryWrapper.orderByAsc(SysMenu::getParentId, SysMenu::getOrderNum);
        List<SysMenu> menus = sysMenuMapper.selectList(queryWrapper);

        // 构建菜单树形结构
        List<MenuDTO> menuTree = buildMenuTree(menus, 0L);

        // 构建返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("menus", menuTree);

        return R.success(result);
    }

    @Override
    public R<Map<String, Object>> roleMenuTreeselect(Long roleId) {
        // 查询所有正常状态的菜单
        LambdaQueryWrapper<SysMenu> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysMenu::getStatus, "0");
        queryWrapper.orderByAsc(SysMenu::getParentId, SysMenu::getOrderNum);
        List<SysMenu> menus = sysMenuMapper.selectList(queryWrapper);

        // 构建菜单树形结构
        List<MenuDTO> menuTree = buildMenuTree(menus, 0L);

        // 查询该角色已拥有的菜单 ID
        List<SysMenu> roleMenus = sysMenuMapper.selectMenuIdsByRoleId(roleId);
        List<Long> checkedKeys = roleMenus.stream()
            .map(SysMenu::getMenuId)
            .collect(java.util.stream.Collectors.toList());

        // 构建返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("menus", menuTree);
        result.put("checkedKeys", checkedKeys);

        return R.success(result);
    }

    @Override
    public R<List<SysMenu>> listMenus(MenuRequest request) {
        // 构建查询条件
        LambdaQueryWrapper<SysMenu> queryWrapper = new LambdaQueryWrapper<>();
        
        // 添加查询条件
        if (StringUtils.hasText(request.getMenuName())) {
            queryWrapper.like(SysMenu::getMenuName, request.getMenuName());
        }
        if (StringUtils.hasText(request.getStatus())) {
            queryWrapper.eq(SysMenu::getStatus, request.getStatus());
        }
        
        // 按父级 ID 和排序号排序
        queryWrapper.orderByAsc(SysMenu::getParentId, SysMenu::getOrderNum);
        
        List<SysMenu> menuList = sysMenuMapper.selectList(queryWrapper);
        return R.success(menuList);
    }

    @Override
    public R<SysMenu> getMenuById(Long menuId) {
        SysMenu menu = getById(menuId);
        if (menu == null) {
            return R.error("菜单不存在");
        }
        return R.success(menu);
    }

    @Override
    public R<Void> addMenu(SysMenu menu) {
        // 校验菜单名称是否已存在
        LambdaQueryWrapper<SysMenu> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysMenu::getMenuName, menu.getMenuName());
        queryWrapper.eq(SysMenu::getParentId, menu.getParentId() != null ? menu.getParentId() : 0L);
        SysMenu existMenu = this.getOne(queryWrapper);
        if (existMenu != null) {
            return R.error("菜单名称已存在");
        }

        // 设置默认值
        if (menu.getStatus() == null || menu.getStatus().isEmpty()) {
            menu.setStatus("0");
        }
        if (menu.getVisible() == null) {
            menu.setVisible(true);
        }
        if (menu.getOrderNum() == null) {
            menu.setOrderNum(0);
        }
        
        menu.setCreatedAt(LocalDateTime.now());
        menu.setUpdatedAt(LocalDateTime.now());

        // 保存菜单
        boolean saved = this.save(menu);
        return saved ? R.success(null) : R.error("新增失败");
    }

    @Override
    public R<Void> updateMenu(SysMenu menu) {
        // 校验菜单是否存在
        SysMenu existMenu = this.getById(menu.getMenuId());
        if (existMenu == null) {
            return R.error("菜单不存在");
        }

        // 校验菜单名称是否已被其他菜单使用
        LambdaQueryWrapper<SysMenu> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysMenu::getMenuName, menu.getMenuName());
        queryWrapper.eq(SysMenu::getParentId, menu.getParentId() != null ? menu.getParentId() : 0L);
        queryWrapper.ne(SysMenu::getMenuId, menu.getMenuId());
        SysMenu duplicateMenu = this.getOne(queryWrapper);
        if (duplicateMenu != null) {
            return R.error("菜单名称已存在");
        }

        // 更新菜单信息
        existMenu.setMenuName(menu.getMenuName());
        existMenu.setParentId(menu.getParentId());
        existMenu.setOrderNum(menu.getOrderNum());
        existMenu.setPath(menu.getPath());
        existMenu.setComponent(menu.getComponent());
        existMenu.setQuery(menu.getQuery());
        existMenu.setIsFrame(menu.getIsFrame());
        existMenu.setIsCache(menu.getIsCache());
        existMenu.setMenuType(menu.getMenuType());
        existMenu.setVisible(menu.getVisible());
        existMenu.setStatus(menu.getStatus());
        existMenu.setPerms(menu.getPerms());
        existMenu.setIcon(menu.getIcon());
        existMenu.setRemark(menu.getRemark());
        existMenu.setUpdatedAt(LocalDateTime.now());

        // 更新菜单
        boolean updated = this.updateById(existMenu);
        return updated ? R.success(null) : R.error("修改失败");
    }

    @Override
    public R<Void> deleteMenu(Long menuId) {
        // 校验菜单是否存在
        SysMenu menu = this.getById(menuId);
        if (menu == null) {
            return R.error("菜单不存在");
        }

        // 检查是否有子菜单
        LambdaQueryWrapper<SysMenu> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysMenu::getParentId, menuId);
        Long count = this.count(queryWrapper);
        if (count > 0) {
            return R.error("存在子菜单，不允许删除");
        }

        // 删除菜单
        boolean deleted = this.removeById(menuId);
        return deleted ? R.success(null) : R.error("删除失败");
    }

    @Override
    public R<Void> updateMenuSort(String menuIds, String orderNums) {
        if (!StringUtils.hasText(menuIds) || !StringUtils.hasText(orderNums)) {
            return R.error("参数不能为空");
        }

        String[] menuIdArray = menuIds.split(",");
        String[] orderNumArray = orderNums.split(",");

        if (menuIdArray.length != orderNumArray.length) {
            return R.error("菜单 ID 和排序号数量不匹配");
        }

        try {
            for (int i = 0; i < menuIdArray.length; i++) {
                Long menuId = Long.parseLong(menuIdArray[i].trim());
                Integer orderNum = Integer.parseInt(orderNumArray[i].trim());

                SysMenu menu = this.getById(menuId);
                if (menu != null) {
                    menu.setOrderNum(orderNum);
                    menu.setUpdatedAt(LocalDateTime.now());
                    this.updateById(menu);
                }
            }
            return R.success(null);
        } catch (NumberFormatException e) {
            log.error("菜单排序更新失败", e);
            return R.error("参数格式错误");
        }
    }

    /**
     * 构建菜单树形结构
     * @param menus 所有菜单列表
     * @param parentId 父级 ID
     * @return 菜单树
     */
    private List<MenuDTO> buildMenuTree(List<SysMenu> menus, Long parentId) {
        return menus.stream()
            .filter(menu -> menu.getParentId().equals(parentId))
            .map(SysMenu::toDTO)
            .peek(dto -> dto.setChildren(buildMenuTree(menus, dto.getId())))
            .collect(Collectors.toList());
    }
}
