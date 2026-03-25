package com.lingxi.isi.models.dto;

import lombok.Data;

/**
 * <p>
 * HR 信息数据传输对象
 * </p>
 *
 * @author lingxi
 * @since 2026-03-25
 */
@Data
public class HrProfileDTO {

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
}
