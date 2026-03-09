package com.lingxi.scs.application.dto;

import lombok.Data;

/**
 * 菜品口味DTO
 *
 * @author system
 */
@Data
public class DishFlavorDTO {
    /**
     * 口味ID
     */
    private Long id;

    /**
     * 口味名称
     */
    private String name;

    /**
     * 口味值
     */
    private String value;
}
