// DishAggregate.java
package com.lingxi.scs.domain.model.aggregate;

import com.lingxi.scs.domain.model.entity.Dish;
import com.lingxi.scs.domain.model.entity.DishFlavor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 菜品聚合根
 * 管理菜品及其口味信息，维护一致性
 */
@Data
public class DishAggregate {

    // 聚合根实体
    private Dish dish;

    // 关联的口味列表
    private List<DishFlavor> flavors = new ArrayList<>();

    // 构造函数
    public DishAggregate(Dish dish) {
        this.dish = dish;
    }

    // 业务方法：添加口味
    public void addFlavor(DishFlavor flavor) {
        if (flavor != null) {
            flavor.setDishId(dish.getId());
            this.flavors.add(flavor);
        }
    }

    // 业务方法：移除口味
    public void removeFlavor(Long flavorId) {
        this.flavors.removeIf(f -> f.getId().equals(flavorId));
    }

    // 业务方法：修改价格（包含业务规则）
    public void changePrice(BigDecimal newPrice) {
        if (newPrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("价格必须大于0");
        }
        this.dish.setPrice(newPrice);
    }

    // 业务方法：启用菜品
    public void enable() {
        if (this.flavors.isEmpty()) {
            throw new IllegalStateException("必须至少有一个口味才能启用");
        }
        this.dish.setStatus(1);
    }

    // 业务方法：停售菜品
    public void disable() {
        this.dish.setStatus(0);
    }
}
