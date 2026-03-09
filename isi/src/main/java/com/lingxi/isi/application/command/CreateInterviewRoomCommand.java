package com.lingxi.isi.application.command;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 创建面试间命令
 */
@Data
public class CreateInterviewRoomCommand {

    @NotNull(message = "简历 ID 不能为空")
    private Long resumeId;

    @NotNull(message = "用户 ID 不能为空")
    private Long userId;

    private String title;

    private Boolean aiEnabled;

    @NotBlank(message = "HR 用户 ID 不能为空")
    private Long hrUserId;

    private String companyId;
}
