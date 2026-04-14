package com.lingxi.isi.models.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.lingxi.isi.models.dto.MenuDTO;
import com.lingxi.isi.models.convert.MenuConvert;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@TableName("sys_menu")
public class SysMenu {
    
    @TableId(value = "menu_id", type = IdType.AUTO)
    private Long menuId;
    
    private String menuName;
    
    private Long parentId;
    
    private Integer orderNum;
    
    private String path;
    
    private String component;
    
    private Integer query;
    
    private Boolean isFrame;
    
    private Boolean isCache;
    
    private String menuType;
    
    private Boolean visible;
    
    private String status;
    
    private String perms;
    
    private String icon;
    
    private String remark;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
    
    /**
     * 转换为 DTO
     */
    public MenuDTO toDTO() {
        return MenuConvert.INSTANCE.convertToDTO(this);
    }
    
    /**
     * 构建菜单树（静态方法，用于递归构建树形结构）
     * 
     * @param menus 所有菜单列表
     * @param parentId 父级 ID
     * @return 指定父级下的子菜单树
     */
    public static List<MenuDTO> buildMenuTree(List<SysMenu> menus, Long parentId) {
        return menus.stream()
            .filter(menu -> menu.getParentId().equals(parentId))
            .map(SysMenu::toDTO)
            .peek(dto -> dto.setChildren(buildMenuTree(menus, dto.getId())))
            .collect(Collectors.toList());
    }
}
