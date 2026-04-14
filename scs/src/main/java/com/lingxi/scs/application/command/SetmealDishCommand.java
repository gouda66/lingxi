package com.lingxi.scs.application.command;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 套餐菜品命令
 */
@Data
public class SetmealDishCommand {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long dishId;
    private String name;
    private BigDecimal price;
    private Integer copies;
}
