package com.lingxi.isi.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lingxi.isi.common.result.R;
import com.lingxi.isi.models.dto.MenuDTO;
import com.lingxi.isi.models.entity.SysMenu;
import com.lingxi.isi.models.request.MenuRequest;

import java.util.List;
import java.util.Map;

/**
 * 菜单服务接口
 */
public interface ISysMenuService extends IService<SysMenu> {

    /**
     * 获取菜单树形结构（用于角色分配）
     * @return 菜单树
     */
    R<Map<String, Object>> treeselect();

    /**
     * 根据角色 ID 查询菜单树形结构
     * @param roleId 角色 ID
     * @return 菜单树和选中的菜单 ID
     */
    R<Map<String, Object>> roleMenuTreeselect(Long roleId);

    /**
     * 查询菜单列表
     * @param request 查询参数
     * @return 菜单列表
     */
    R<List<SysMenu>> listMenus(MenuRequest request);

    /**
     * 根据 ID 获取菜单详情
     * @param menuId 菜单 ID
     * @return 菜单详情
     */
    R<SysMenu> getMenuById(Long menuId);

    /**
     * 新增菜单
     * @param menu 菜单信息
     * @return 操作结果
     */
    R<Void> addMenu(SysMenu menu);

    /**
     * 修改菜单
     * @param menu 菜单信息
     * @return 操作结果
     */
    R<Void> updateMenu(SysMenu menu);

    /**
     * 删除菜单
     * @param menuId 菜单 ID
     * @return 操作结果
     */
    R<Void> deleteMenu(Long menuId);

    /**
     * 批量更新菜单排序
     * @param menuIds 菜单 ID 列表（逗号分隔）
     * @param orderNums 排序号列表（逗号分隔）
     * @return 操作结果
     */
    R<Void> updateMenuSort(String menuIds, String orderNums);
}
