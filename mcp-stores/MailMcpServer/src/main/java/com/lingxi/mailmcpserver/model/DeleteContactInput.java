package com.lingxi.mailmcpserver.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jdk.jfr.Description;
import lombok.Builder;
import lombok.Data;
import org.springframework.ai.tool.annotation.ToolParam;

@Data
@Builder
@Description("删除联系人请求参数")
public class DeleteContactInput {

    @JsonProperty(required = true)
    @Description("联系人姓名")
    @ToolParam(description = "联系人姓名")
    private String name;
}
