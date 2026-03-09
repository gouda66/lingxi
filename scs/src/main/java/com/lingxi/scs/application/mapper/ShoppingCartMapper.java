package com.lingxi.scs.application.mapper;

import com.lingxi.scs.application.dto.ShoppingCartDTO;
import com.lingxi.scs.domain.model.entity.ShoppingCart;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 购物车映射器
 * 
 * <p>Mapper模式说明：</p>
 * <ul>
 *     <li>负责Entity与DTO之间的转换</li>
 *     <li>隔离领域模型和外部表示</li>
 *     <li>保持领域模型的纯净性</li>
 *     <li>可以使用MapStruct等工具简化实现</li>
 *     <li>命名规范：实体名 + Mapper</li>
 * </ul>
 *
 * @author system
 */
@Component
public class ShoppingCartMapper {

    /**
     * 将实体转换为DTO
     * 
     * @param entity 购物车实体
     * @return 购物车DTO
     */
    public ShoppingCartDTO toDTO(ShoppingCart entity) {
        if (entity == null) {
            return null;
        }

        ShoppingCartDTO dto = new ShoppingCartDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setUserId(entity.getUserId());
        dto.setDishId(entity.getDishId());
        dto.setSetmealId(entity.getSetmealId());
        dto.setDishFlavor(entity.getDishFlavor());
        dto.setNumber(entity.getNumber());
        dto.setAmount(entity.getAmount());
        dto.setImage(entity.getImage());
        dto.setCreateTime(entity.getCreateTime());

        // 设置商品类型
        if (entity.getDishId() != null) {
            dto.setItemType("DISH");
        } else if (entity.getSetmealId() != null) {
            dto.setItemType("SETMEAL");
        }

        // 计算小计
        dto.calculateSubtotal();

        return dto;
    }

    /**
     * 批量将实体列表转换为DTO列表
     * 
     * @param entities 实体列表
     * @return DTO列表
     */
    public List<ShoppingCartDTO> toDTOList(List<ShoppingCart> entities) {
        if (entities == null) {
            return null;
        }

        return entities.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * 将DTO转换为实体
     * 注意：通常不需要此方法，因为写操作使用Command，而非DTO
     * 这里仅作示例展示
     * 
     * @param dto 购物车DTO
     * @return 购物车实体
     */
    public ShoppingCart toEntity(ShoppingCartDTO dto) {
        if (dto == null) {
            return null;
        }

        ShoppingCart entity = new ShoppingCart();
        entity.setId(dto.getId());
        entity.setName(dto.getName());
        entity.setUserId(dto.getUserId());
        entity.setDishId(dto.getDishId());
        entity.setSetmealId(dto.getSetmealId());
        entity.setDishFlavor(dto.getDishFlavor());
        entity.setNumber(dto.getNumber());
        entity.setAmount(dto.getAmount());
        entity.setImage(dto.getImage());
        entity.setCreateTime(dto.getCreateTime());

        return entity;
    }
}
