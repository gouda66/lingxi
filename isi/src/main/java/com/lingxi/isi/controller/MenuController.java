package com.lingxi.isi.controller;

import com.lingxi.isi.common.result.R;
import com.lingxi.isi.models.entity.SysMenu;
import com.lingxi.isi.models.request.MenuRequest;
import com.lingxi.isi.service.ISysMenuService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 菜单管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/system/menu")
public class MenuController {

    private final ISysMenuService menuService;

    public MenuController(ISysMenuService menuService) {
        this.menuService = menuService;
    }

    /**
     * 获取菜单树形结构（用于角色分配）
     */
    @GetMapping("/treeselect")
    public R<Map<String, Object>> treeselect() {
        return menuService.treeselect();
    }

    /**
     * 根据角色 ID 查询菜单树形结构
     */
    @GetMapping("/roleMenuTreeselect/{roleId}")
    public R<Map<String, Object>> roleMenuTreeselect(@PathVariable Long roleId) {
        return menuService.roleMenuTreeselect(roleId);
    }

    /**
     * 查询菜单列表
     */
    @GetMapping("/list")
    public R<List<SysMenu>> list(MenuRequest request) {
        return menuService.listMenus(request);
    }

    /**
     * 根据 ID 获取菜单详情
     */
    @GetMapping("/{menuId}")
    public R<SysMenu> getInfo(@PathVariable Long menuId) {
        return menuService.getMenuById(menuId);
    }

    /**
     * 新增菜单
     */
    @PostMapping
    public R add(@RequestBody SysMenu menu) {
        return menuService.addMenu(menu);
    }

    /**
     * 修改菜单
     */
    @PutMapping
    public R edit(@RequestBody SysMenu menu) {
        return menuService.updateMenu(menu);
    }

    /**
     * 删除菜单
     */
    @DeleteMapping("/{menuId}")
    public R remove(@PathVariable Long menuId) {
        return menuService.deleteMenu(menuId);
    }

    /**
     * 批量更新菜单排序
     */
    @PutMapping("/updateSort")
    public R updateSort(@RequestBody Map<String, String> params) {
        String menuIds = params.get("menuIds");
        String orderNums = params.get("orderNums");
        return menuService.updateMenuSort(menuIds, orderNums);
    }
}
