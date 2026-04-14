package com.lingxi.mailmcpserver.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jdk.jfr.Description;
import lombok.Builder;
import lombok.Data;
import org.springframework.ai.tool.annotation.ToolParam;

/**
 * @author zhangshdiong
 * @date 2025/5/9 10:48
 * @description 查找联系人请求实体
 **/
@Data
@Builder
@Description("查找联系人请求参数")
public class FindContactInput {

    @JsonProperty(required = true)
    @Description("联系人姓名")
    @ToolParam(description = "联系人姓名")
    private String name;
}
