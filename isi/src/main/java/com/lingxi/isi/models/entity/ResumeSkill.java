package com.lingxi.isi.models.entity;
import java.io.Serial;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 简历技能表
 * </p>
 *
 * @author lingxi
 * @since 2026-03-25
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("resume_skill")
public class ResumeSkill implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键 ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 简历 ID
     */
    private Long resumeId;

    /**
     * 技能名称
     */
    private String skillName;

    /**
     * 熟练度：1-入门 2-熟练 3-精通 4-专家
     */
    private Integer proficiency;

    /**
     * 分类：语言/框架/工具/数据库等
     */
    private String category;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;


}
