package com.lingxi.isi.application.service;

import com.lingxi.isi.application.dto.MenuDTO;
import com.lingxi.isi.application.mapper.MenuMapper;
import com.lingxi.isi.domain.model.entity.Menu;
import com.lingxi.isi.domain.repository.MenuRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 菜单应用服务
 */
@Service
@RequiredArgsConstructor
public class MenuApplicationService {

    private final MenuRepository menuRepository;

    /**
     * 获取用户的菜单树（根据用户权限）
     */
    public List<MenuDTO> getMenusByUserId(Long userId) {
        // 查询用户的菜单权限
        List<Menu> menus = menuRepository.findMenusByUserId(userId);
        
        // 转换为 DTO 并构建树形结构
        List<MenuDTO> menuDTOs = MenuMapper.INSTANCE.toDTOList(menus);
        
        return buildMenuTree(menuDTOs, 0L);
    }

    /**
     * 获取所有可见菜单
     */
    public List<MenuDTO> getAllHiddenMenus() {
        List<Menu> menus = menuRepository.findByHiddenOrderByOrderNum(0);
        List<MenuDTO> menuDTOs = MenuMapper.INSTANCE.toDTOList(menus);
        return buildMenuTree(menuDTOs, 0L);
    }

    /**
     * 构建菜单树
     */
    private List<MenuDTO> buildMenuTree(List<MenuDTO> menus, Long parentId) {
        return menus.stream()
                .filter(menu -> parentId.equals(menu.getParentId()))
                .peek(menu -> {
                    // 递归设置子菜单
                    List<MenuDTO> children = buildMenuTree(menus, menu.getId());
                    if (!children.isEmpty()) {
                        menu.setChildren(children);
                    }
                })
                .sorted(Comparator.comparing(MenuDTO::getOrderNum))
                .collect(Collectors.toList());
    }
}
