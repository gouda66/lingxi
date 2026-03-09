package com.lingxi.isi.application.command;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 生成 AI 评估命令
 */
@Data
public class GenerateEvaluationCommand {

    @NotNull(message = "面试间 ID 不能为空")
    private Long roomId;

    @NotNull(message = "简历 ID 不能为空")
    private Long resumeId;

    @NotNull(message = "用户 ID 不能为空")
    private Long userId;

    @NotBlank(message = "面试记录不能为空")
    private String interviewRecords;

    private Boolean aiGenerated;
}
