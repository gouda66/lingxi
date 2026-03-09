package com.lingxi.scs.application.command;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 菜品口味内嵌命令
 */
@Data
public class DishFlavorCommand {
    /**
     * 口味名称
     */
    @NotBlank(message = "口味名称不能为空")
    private String name;

    /**
     * 口味值（JSON格式）
     */
    @NotBlank(message = "口味值不能为空")
    private String value;
}