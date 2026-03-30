package com.lingxi.isi.models.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 系统通知公告表
 */
@Data
@TableName("sys_notice")
public class SysNotice {
    
    @TableId(value = "notice_id", type = IdType.AUTO)
    private Long noticeId;
    
    private String noticeTitle;
    
    private String noticeType;
    
    private String noticeContent;
    
    private String status;
    
    private String creator;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
    
    private String remark;
}
