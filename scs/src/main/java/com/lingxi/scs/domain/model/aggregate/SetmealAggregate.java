// SetmealAggregate.java
package com.lingxi.scs.domain.model.aggregate;

import com.lingxi.scs.domain.model.entity.Setmeal;
import com.lingxi.scs.domain.model.entity.SetmealDish;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 套餐聚合根
 * 管理套餐及其包含的菜品，维护一致性
 */
@Data
public class SetmealAggregate {

    // 聚合根实体
    private Setmeal setmeal;

    // 套餐包含的菜品列表
    private List<SetmealDish> setmealDishes = new ArrayList<>();

    // 构造函数
    public SetmealAggregate(Setmeal setmeal) {
        this.setmeal = setmeal;
    }

    // 业务方法：添加菜品到套餐
    public void addDish(SetmealDish setmealDish) {
        if (setmealDish != null) {
            setmealDish.setSetmealId(setmeal.getId());
            this.setmealDishes.add(setmealDish);
        }
    }

    // 业务方法：移除菜品
    public void removeDish(Long dishId) {
        this.setmealDishes.removeIf(d -> d.getDishId().equals(dishId));
    }

    // 业务方法：计算套餐总价（业务规则）
    public BigDecimal calculateTotalPrice() {
        return setmealDishes.stream()
            .map(SetmealDish::getPrice)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // 业务方法：启用套餐
    public void enable() {
        if (setmealDishes.isEmpty()) {
            throw new IllegalStateException("套餐必须包含至少一个菜品");
        }
        this.setmeal.setStatus(1);
    }

    // 业务方法：停用套餐
    public void disable() {
        this.setmeal.setStatus(0);
    }
}
