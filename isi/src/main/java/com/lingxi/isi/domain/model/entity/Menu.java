package com.lingxi.isi.domain.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 菜单实体类
 */
@Data
@Entity
@Table(name = "sys_menu")
public class Menu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 菜单名称
     */
    private String menuName;

    /**
     * 父菜单 ID
     */
    private Long parentId;

    /**
     * 显示顺序
     */
    private Integer orderNum;

    /**
     * 路由地址
     */
    private String path;

    /**
     * 组件路径（如：system/user/index）
     */
    private String component;

    /**
     * 是否为外链（0 是 1 否）
     */
    private Integer isFrame;

    /**
     * 是否缓存（0 缓存 1 不缓存）
     */
    private Integer isCache;

    /**
     * 类型（M 目录 C 菜单 F 按钮）
     */
    private String menuType;

    /**
     * 显示状态（0 显示 1 隐藏）
     */
    private Integer hidden;

    /**
     * 菜单图标
     */
    private String icon;

    /**
     * 权限标识
     */
    private String perms;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
