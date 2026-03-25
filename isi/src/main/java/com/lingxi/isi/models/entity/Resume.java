package com.lingxi.isi.models.entity;
import java.io.Serial;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * <p>
 * 简历表
 * </p>
 *
 * @author lingxi
 * @since 2026-03-25
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("resume")
public class Resume implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键 ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 所属用户 ID
     */
    private Long userId;

    /**
     * 简历名称（如：Java 开发工程师 - 张三）
     */
    private String resumeName;

    /**
     * 原始文件 URL（PDF/DOCX）
     */
    private String originalFileUrl;

    /**
     * 文件类型：pdf/docx/txt
     */
    private String fileType;

    /**
     * 文件大小（字节）
     */
    private Integer fileSize;

    /**
     * 解析状态：0-待解析 1-解析中 2-解析成功 3-解析失败
     */
    private Integer parseStatus;

    /**
     * 解析错误信息
     */
    private String parseErrorMsg;

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
     * 教育背景：[{school, major, degree, startDate, endDate}]
     */
    private String educationJson;

    /**
     * 工作年限
     */
    private Integer workYears;

    /**
     * 是否为默认简历：0-否 1-是
     */
    private Integer isDefault;

    /**
     * 版本号（简历更新次数）
     */
    private Integer version;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    /**
     * 逻辑删除：0-未删除 1-已删除
     */
    @TableLogic
    private Integer deleted;


}
