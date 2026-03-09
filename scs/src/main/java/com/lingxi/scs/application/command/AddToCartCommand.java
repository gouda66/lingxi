package com.lingxi.scs.application.command;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 添加商品到购物车命令
 * 
 * <p>Command模式说明：</p>
 * <ul>
 *     <li>封装用户操作意图的输入参数</li>
 *     <li>用于写操作（增、删、改）</li>
 *     <li>包含参数验证注解</li>
 *     <li>命名规范：动词 + 名词 + Command</li>
 * </ul>
 *
 * @author system
 */
@Data
public class AddToCartCommand {

    /**
     * 菜品ID（菜品和套餐二选一）
     */
    private Long dishId;

    /**
     * 套餐ID（菜品和套餐二选一）
     */
    private Long setmealId;

    /**
     * 菜品口味（仅当添加菜品时使用）
     */
    private String dishFlavor;

    /**
     * 数量
     */
    @NotNull(message = "数量不能为空")
    @Min(value = 1, message = "数量至少为1")
    private Integer number;

    /**
     * 验证命令的业务规则
     * 
     * @throws IllegalArgumentException 如果验证失败
     */
    public void validate() {
        if (dishId == null && setmealId == null) {
            throw new IllegalArgumentException("菜品ID和套餐ID不能同时为空");
        }
        if (dishId != null && setmealId != null) {
            throw new IllegalArgumentException("不能同时添加菜品和套餐");
        }
    }
}
