package com.lingxi.scs.application.mapper;

import com.lingxi.scs.domain.model.entity.Setmeal;
import com.lingxi.scs.domain.model.entity.SetmealDish;
import com.lingxi.scs.application.dto.vo.SetmealDetailVO;
import com.lingxi.scs.application.dto.vo.SetmealDishVO;
import com.lingxi.scs.application.dto.vo.SetmealListVO;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 套餐映射器
 * 负责套餐相关对象的转换
 *
 * @author system
 */
@Component
public class SetmealMapper {

    /**
     * 转换套餐实体为详情VO
     */
    public SetmealDetailVO toDetailVO(Setmeal setmeal, String categoryName, List<SetmealDishVO> dishVOs) {
        return new SetmealDetailVO(
                setmeal.getId(),
                setmeal.getName(),
                setmeal.getCategoryId(),
                categoryName,
                setmeal.getPrice(),
                setmeal.getImage(),
                setmeal.getDescription(),
                setmeal.getStatus(),
                dishVOs
        );
    }

    /**
     * 转换套餐实体为列表VO
     */
    public SetmealListVO toListVO(Setmeal setmeal, String categoryName, int dishCount) {
        return new SetmealListVO(
                setmeal.getId(),
                setmeal.getName(),
                setmeal.getCategoryId(),
                categoryName,
                setmeal.getPrice(),
                setmeal.getImage(),
                setmeal.getDescription(),
                setmeal.getStatus(),
                dishCount
        );
    }

    /**
     * 转换套餐菜品为VO
     */
    public SetmealDishVO toDishVO(SetmealDish setmealDish, String dishImage) {
        return new SetmealDishVO(
                setmealDish.getDishId(),
                setmealDish.getName(),
                setmealDish.getPrice(),
                setmealDish.getCopies(),
                dishImage
        );
    }
}