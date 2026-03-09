package com.lingxi.scs.application.command;

import com.lingxi.scs.application.dto.SetmealDTO;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 套餐操作命令
 *
 * @author system
 */
@Data
public class SetmealCommand extends SetmealDTO{

    /**
     * 套餐菜品列表
     */
    private List<SetmealDishCommand> setmealDishes;
}
