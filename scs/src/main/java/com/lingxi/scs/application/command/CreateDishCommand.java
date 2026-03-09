package com.lingxi.scs.application.command;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 创建菜品命令
 * 展示复杂对象的Command设计
 *
 * @author system
 */
@Data
public class CreateDishCommand {

    /**
     * 菜品名称
     */
    @NotBlank(message = "菜品名称不能为空")
    @Size(max = 64, message = "菜品名称长度不能超过64个字符")
    private String name;

    /**
     * 分类ID
     */
    @NotNull(message = "分类ID不能为空")
    private Long categoryId;

    /**
     * 价格
     */
    @NotNull(message = "价格不能为空")
    @DecimalMin(value = "0.01", message = "价格必须大于0")
    private BigDecimal price;

    /**
     * 商品编码
     */
    @NotBlank(message = "商品编码不能为空")
    private String code;

    /**
     * 图片路径
     */
    @NotBlank(message = "图片不能为空")
    private String image;

    /**
     * 描述信息
     */
    @Size(max = 400, message = "描述信息长度不能超过400个字符")
    private String description;

    /**
     * 排序顺序
     */
    @Min(value = 0, message = "排序值不能为负数")
    private Integer sort = 0;

    /**
     * 菜品口味列表
     */
    private List<DishFlavorCommand> flavors;


}
