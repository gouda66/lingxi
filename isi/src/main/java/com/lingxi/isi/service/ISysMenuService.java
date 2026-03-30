package com.lingxi.isi.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lingxi.isi.common.result.R;
import com.lingxi.isi.models.dto.MenuDTO;
import com.lingxi.isi.models.entity.SysMenu;

import java.util.List;

public interface ISysMenuService extends IService<SysMenu> {
    
    R<List<MenuDTO>> getMenuTree();
    
    List<MenuDTO> buildMenuTree(List<SysMenu> menus, Long parentId);
}
