package com.lingxi.isi.domain.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 角色菜单关联表
 */
@Data
@Entity
@Table(name = "sys_role_menu")
public class RoleMenu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 角色 ID
     */
    private Long roleId;

    /**
     * 菜单 ID
     */
    private Long menuId;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
