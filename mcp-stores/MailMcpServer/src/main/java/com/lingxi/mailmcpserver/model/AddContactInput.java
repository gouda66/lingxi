package com.lingxi.mailmcpserver.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jdk.jfr.Description;
import lombok.Builder;
import lombok.Data;
import org.springframework.ai.tool.annotation.ToolParam;

@Data
@Builder
@Description("添加联系人请求参数")
public class AddContactInput {

    @JsonProperty(required = true)
    @Description("联系人姓名")
    @ToolParam(description = "联系人姓名")
    private String name;

    @JsonProperty(required = true)
    @Description("邮箱地址")
    @ToolParam(description = "邮箱地址")
    private String email;
}
