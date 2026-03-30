package com.lingxi.isi.models.dto;

import lombok.Data;

import java.util.List;

@Data
public class MenuDTO {
    
    private Long id;
    
    private String name;
    
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
    
    private List<String> permissions;
    
    private String icon;
    
    private String title;
    
    private Boolean hidden;
    
    private List<MenuDTO> children;
}
