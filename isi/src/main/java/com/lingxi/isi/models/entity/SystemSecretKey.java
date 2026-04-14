package com.lingxi.isi.models.entity;


import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("system_secret_key")
public class SystemSecretKey {
    
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    private String keyName;
    
    private String keyValue;
    
    private String algorithm;
    
    private Integer isActive;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
}
