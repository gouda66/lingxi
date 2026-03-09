package com.lingxi.scs.application.command;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 套餐菜品命令
 */
@Data
public class SetmealDishCommand {
    private Long dishId;
    private String name;
    private BigDecimal price;
    private Integer copies;
}
