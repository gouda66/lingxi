package com.lingxi.scs.infrastructure.adapter;

import com.lingxi.scs.domain.service.SmsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 阿里云短信服务适配器
 * 将阿里云短信API适配成领域层定义的SmsService接口
 *
 * @author system
 */
@Slf4j
@Component
public class AliyunSmsAdapter implements SmsService {

    // 实际项目中这些配置应该从配置文件读取
    private static final String ACCESS_KEY_ID = "your-access-key-id";
    private static final String ACCESS_KEY_SECRET = "your-access-key-secret";
    private static final String SIGN_NAME = "灵犀外卖";
    private static final String VERIFICATION_CODE_TEMPLATE = "SMS_123456789";
    private static final String ORDER_NOTIFICATION_TEMPLATE = "SMS_987654321";

    @Override
    public boolean sendVerificationCode(String phone, String code) {
        try {
            log.info("发送验证码短信: phone={}, code={}", phone, code);
            
            // 实际项目中调用阿里云SDK
            // DefaultProfile profile = DefaultProfile.getProfile("cn-hangzhou", ACCESS_KEY_ID, ACCESS_KEY_SECRET);
            // IAcsClient client = new DefaultAcsClient(profile);
            // SendSmsRequest request = new SendSmsRequest();
            // request.setPhoneNumbers(phone);
            // request.setSignName(SIGN_NAME);
            // request.setTemplateCode(VERIFICATION_CODE_TEMPLATE);
            // request.setTemplateParam("{\"code\":\"" + code + "\"}");
            // SendSmsResponse response = client.getAcsResponse(request);
            // return "OK".equals(response.getCode());
            
            // 模拟发送成功
            log.info("短信发送成功: 您的验证码是{}，5分钟内有效", code);
            return true;
            
        } catch (Exception e) {
            log.error("发送验证码短信失败: phone={}", phone, e);
            return false;
        }
    }

    @Override
    public boolean sendOrderNotification(String phone, String orderNumber, String message) {
        try {
            log.info("发送订单通知短信: phone={}, orderNumber={}, message={}", 
                    phone, orderNumber, message);
            
            // 实际项目中调用阿里云SDK
            // 类似上面的实现，使用不同的模板
            
            // 模拟发送成功
            log.info("订单通知短信发送成功: 订单{}已{}", orderNumber, message);
            return true;
            
        } catch (Exception e) {
            log.error("发送订单通知短信失败: phone={}, orderNumber={}", phone, orderNumber, e);
            return false;
        }
    }

    /**
     * 验证手机号格式
     */
    private boolean isValidPhone(String phone) {
        return phone != null && phone.matches("^1[3-9]\\d{9}$");
    }
}
