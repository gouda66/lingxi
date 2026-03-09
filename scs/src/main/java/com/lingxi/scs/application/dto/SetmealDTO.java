package com.lingxi.scs.application.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 套餐数据传输对象
 *
 * @author system
 */
@Data
public class SetmealDTO {
    private Long id;
    private Long categoryId;
    private String name;
    private BigDecimal price;
    private Integer status;
    private String code;
    private String description;
    private String image;
}
