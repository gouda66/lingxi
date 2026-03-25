package com.lingxi.isi.models.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * <p>
 * HR 信息视图对象
 * </p>
 *
 * @author lingxi
 * @since 2026-03-25
 */
@Data
public class HrProfileVO {

    /**
     * 主键 ID
     */
    private Long id;

    /**
     * 关联用户 ID
     */
    private Long userId;

    /**
     * 公司名称
     */
    private String companyName;

    /**
     * 部门
     */
    private String department;

    /**
     * 职位
     */
    private String position;

    /**
     * 工作邮箱
     */
    private String workEmail;

    /**
     * 是否接收通知
     */
    private Integer notificationEnabled;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}
