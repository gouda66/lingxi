package com.lingxi.isi.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lingxi.isi.common.result.R;
import com.lingxi.isi.mapper.SysMenuMapper;
import com.lingxi.isi.models.convert.MenuConvert;
import com.lingxi.isi.models.dto.MenuDTO;
import com.lingxi.isi.models.entity.SysMenu;
import com.lingxi.isi.service.ISysMenuService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements ISysMenuService {
    
    private final SysMenuMapper sysMenuMapper;
    
    public SysMenuServiceImpl(SysMenuMapper sysMenuMapper) {
        this.sysMenuMapper = sysMenuMapper;
    }
    
    @Override
    public R<List<MenuDTO>> getMenuTree() {
        // 获取所有启用的菜单
        List<SysMenu> menus = sysMenuMapper.selectMenuTree();
        
        // 构建树形结构
        List<MenuDTO> menuTree = buildMenuTree(menus, 0L);
        
        return R.success(menuTree);
    }
    
    @Override
    public List<MenuDTO> buildMenuTree(List<SysMenu> menus, Long parentId) {
        return menus.stream()
            .filter(menu -> menu.getParentId().equals(parentId))
            .map(MenuConvert.INSTANCE::convertToDTO)
            .peek(dto -> dto.setChildren(buildMenuTree(menus, dto.getId())))
            .collect(java.util.stream.Collectors.toList());
    }
}
