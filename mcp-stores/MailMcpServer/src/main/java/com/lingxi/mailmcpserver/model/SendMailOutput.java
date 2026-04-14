package com.lingxi.mailmcpserver.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zhangshdiong
 * @date 2025/5/9 10:48
 * @description 发送邮件响应实体
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SendMailOutput {

    /**
     * 结果码：00-成功，其他-失败
     */
    private String resultCode;

    /**
     * 结果消息
     */
    private String resultMsg;
}
