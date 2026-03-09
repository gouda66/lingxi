package com.lingxi.scs.application.dto.vo;

/**
 * 分类VO
 *
 * @author system
 * @param id ID
 * @param name 名称
 * @param type 类型
 * @param sort 排序
 */
public record CategoryVO(
        Long id,
        String name,
        Integer type,
        Integer sort
) {}