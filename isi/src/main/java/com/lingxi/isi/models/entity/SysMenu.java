package com.lingxi.isi.models.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

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
}
