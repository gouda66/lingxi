package com.lingxi.isi.application.command;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 创建简历命令
 */
@Data
public class CreateResumeCommand {

    @NotNull(message = "用户 ID 不能为空")
    private Long userId;

    @NotBlank(message = "简历标题不能为空")
    private String title;

    @NotBlank(message = "文件路径不能为空")
    private String filePath;

    private String fileName;

    private String fileType;

    private String contentText;

    private String structuredData;

    private String skills;

    private Integer experienceYears;

    private String education;

    private String major;

    private String expectedPosition;

    private String expectedSalary;
}
