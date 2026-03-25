package com.lingxi.isi.models.vo;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * <p>
 * 简历视图对象
 * </p>
 *
 * @author lingxi
 * @since 2026-03-25
 */
@Data
public class ResumeVO {

    /**
     * 简历 ID
     */
    private Long id;

    /**
     * 所属用户 ID
     */
    private Long userId;

    /**
     * 简历名称
     */
    private String resumeName;

    /**
     * 原始文件 URL
     */
    private String originalFileUrl;

    /**
     * 文件类型
     */
    private String fileType;

    /**
     * 文件大小
     */
    private Integer fileSize;

    /**
     * 解析状态
     */
    private Integer parseStatus;

    /**
     * 姓名
     */
    private String fullName;

    /**
     * 性别
     */
    private Integer gender;

    /**
     * 出生日期
     */
    private LocalDate birthDate;

    /**
     * 电话
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 所在地
     */
    private String location;

    /**
     * 求职意向
     */
    private String jobTitle;

    /**
     * 期望薪资
     */
    private String expectedSalary;

    /**
     * 自我评价
     */
    private String selfEvaluation;

    /**
     * 教育背景 JSON
     */
    private String educationJson;

    /**
     * 工作年限
     */
    private Integer workYears;

    /**
     * 是否为默认简历
     */
    private Integer isDefault;

    /**
     * 版本号
     */
    private Integer version;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}
