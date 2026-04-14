package com.lingxi.scs.application.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 菜品数据传输对象
 * 用于返回菜品详细信息（包含口味列表）
 *
 * @author system
 */
@Data
public class DishDTO {

    /**
     * 菜品ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 菜品名称
     */
    private String name;

    /**
     * 分类ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long categoryId;

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
     * 设置状态文本
     */
    public void setStatusText() {
        this.statusText = (status != null && status == 1) ? "起售" : "停售";
    }
}