package com.lingxi.scs.application.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * MCP Tool 专用的菜品 DTO
 * 所有 Long 类型字段都转换为 String，避免 JavaScript 精度丢失
 *
 * @author system
 */
@Data
public class McpDishDTO {

    /**
     * 菜品ID（String类型，避免JS精度丢失）
     */
    private String id;

    /**
     * 菜品名称
     */
    private String name;

    /**
     * 分类ID（String类型）
     */
    private String categoryId;

    /**
     * 分类名称
     */
    private String categoryName;

    /**
     * 价格
     */
    private BigDecimal price;

    /**
     * 商品编码
     */
    private String code;

    /**
     * 图片
     */
    private String image;

    /**
     * 描述
     */
    private String description;

    /**
     * 状态（0-停售 1-起售）
     */
    private Integer status;

    /**
     * 状态描述
     */
    private String statusText;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 口味列表
     */
    private List<DishFlavorDTO> flavors;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 从 DishDTO 转换
     */
    public static McpDishDTO from(DishDTO dishDTO) {
        if (dishDTO == null) {
            return null;
        }
        McpDishDTO mcpDishDTO = new McpDishDTO();
        mcpDishDTO.setId(dishDTO.getId() != null ? String.valueOf(dishDTO.getId()) : null);
        mcpDishDTO.setName(dishDTO.getName());
        mcpDishDTO.setCategoryId(dishDTO.getCategoryId() != null ? String.valueOf(dishDTO.getCategoryId()) : null);
        mcpDishDTO.setCategoryName(dishDTO.getCategoryName());
        mcpDishDTO.setPrice(dishDTO.getPrice());
        mcpDishDTO.setCode(dishDTO.getCode());
        mcpDishDTO.setImage(dishDTO.getImage());
        mcpDishDTO.setDescription(dishDTO.getDescription());
        mcpDishDTO.setStatus(dishDTO.getStatus());
        mcpDishDTO.setStatusText(dishDTO.getStatusText());
        mcpDishDTO.setSort(dishDTO.getSort());
        mcpDishDTO.setFlavors(dishDTO.getFlavors());
        mcpDishDTO.setCreateTime(dishDTO.getCreateTime());
        mcpDishDTO.setUpdateTime(dishDTO.getUpdateTime());
        return mcpDishDTO;
    }
}
