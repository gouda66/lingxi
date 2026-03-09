package com.lingxi.scs.domain.service;

/**
 * 短信服务接口（领域层定义）
 * 定义发送短信的能力，不依赖具体实现
 *
 * @author system
 */
public interface SmsService {

    /**
     * 发送短信验证码
     *
     * @param phone 手机号
     * @param code 验证码
     * @return 是否发送成功
     */
    boolean sendVerificationCode(String phone, String code);

    /**
     * 发送订单通知短信
     *
     * @param phone 手机号
     * @param orderNumber 订单号
     * @param message 消息内容
     * @return 是否发送成功
     */
    boolean sendOrderNotification(String phone, String orderNumber, String message);
}
