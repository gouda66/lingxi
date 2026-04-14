package com.lingxi.mailmcpserver.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zhangshdiong
 * @date 2025/5/9 10:48
 * @description 查找联系人响应实体
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FindContactOutput {

    /**
     * 联系人姓名
     */
    private String name;

    /**
     * 邮箱地址
     */
    private String email;

    /**
     * 结果码：00-成功，其他-失败
     */
    private String resultCode;

    /**
     * 结果消息
     */
    private String resultMsg;
}
