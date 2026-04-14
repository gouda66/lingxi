package com.lingxi.isi.models.dto;

import lombok.Data;

import java.time.LocalDate;

/**
 * <p>
 * 简历数据传输对象
 * </p>
 *
 * @author lingxi
 * @since 2026-03-25
 */
@Data
public class ResumeDTO {

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
     * 文件类型：pdf/docx/txt
     */
    private String fileType;

    /**
     * 文件大小（字节）
     */
    private Integer fileSize;

    /**
     * 姓名
     */
    private String fullName;

    /**
     * 性别：0-女 1-男
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
     * 求职意向/目标职位
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
}
