package com.lingxi.scs.interfaces.rest;

import co.paralleluniverse.fibers.Fiber;
import co.paralleluniverse.fibers.SuspendExecution;
import co.paralleluniverse.strands.Strand;
import com.lingxi.scs.application.service.UserApplicationService;
import com.lingxi.scs.common.result.R;
import com.lingxi.scs.domain.model.entity.User;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

/**
 * 用户管理
 *
 * @author system
 */
@Slf4j
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserApplicationService userService;

    /**
     * 发送手机短信验证码
     */
    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession session) {
        String phone = user.getPhone();

        if (phone != null && !phone.isEmpty()) {
            // 生成随机的4位验证码
            String code = String.format("%06d", new Random().nextInt(10000));
            log.info("发送验证码: phone={}, code={}", phone, code);

            // 将验证码保存到Session
            session.setAttribute(phone, code);

            return R.success("手机验证码短信发送成功");
        }

        return R.error("短信发送失败");
    }

    /**
     * 移动端用户登录
     */
    @PostMapping("/login")
    public R<User> login(@RequestBody Map<String, String> map, HttpSession session) {
        log.info("用户登录: {}", map);

        String phone = map.get("phone");
        String code = map.get("code");
        // 从Session中获取保存的验证码
        Object codeInSession = session.getAttribute(phone);
        CompletableFuture completableFuture = new CompletableFuture();
        // 验证码比对
        if (codeInSession != null && codeInSession.equals(code)) {
            // 登录成功，查询或创建用户
            User user = userService.getOrCreateUserByPhone(phone);
            session.setAttribute("user", user.getId());
            return R.success(user);
        }
        return R.error("登录失败");
    }

    /**
     * 用户退出
     */
    @PostMapping("/logout")
    public R<String> logout(HttpSession session) {
        session.removeAttribute("user");
        return R.success("退出成功");
    }
}
