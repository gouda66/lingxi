package com.lingxi.scs.interfaces.rest;

import ch.qos.logback.core.util.MD5Util;
import co.paralleluniverse.fibers.Fiber;
import co.paralleluniverse.fibers.SuspendExecution;
import co.paralleluniverse.strands.Strand;
import com.lingxi.scs.application.service.UserApplicationService;
import com.lingxi.scs.common.result.R;
import com.lingxi.scs.domain.model.entity.Employee;
import com.lingxi.scs.domain.model.entity.User;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
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
            // 生成随机的6位验证码
            String code = String.format("%06d", new Random().nextInt(1000000));
            log.info("发送验证码: phone={}, code={}", phone, code);

            // 将验证码保存到Session
            session.setAttribute(phone, code);

            // 调用推送助手发送短信
            try {
                sendSmsViaPushHelper(phone, code);
                return R.success("手机验证码短信发送成功");
            } catch (Exception e) {
                log.error("短信发送失败: {}", e.getMessage());
                return R.error("短信发送失败: " + e.getMessage());
            }
        }

        return R.error("短信发送失败：手机号不能为空");
    }

    /**
     * 通过推送助手发送短信
     */
    private void sendSmsViaPushHelper(String phone, String code) throws Exception {
        // 推送助手的API地址（需要替换为实际的token）
        String pushUrl = "https://push.spug.cc/send/k2RVBmyzanj0ny3b";
        
        // 构建请求参数
        String postData = String.format("name=推送助手&code=%s&targets=%s", code, phone);
        
        // 创建HTTP连接
        URL url = new URL(pushUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        
        try {
            // 设置请求方法
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            
            // 发送请求数据
            try (OutputStream os = conn.getOutputStream()) {
                os.write(postData.getBytes("UTF-8"));
                os.flush();
            }
            
            // 获取响应码
            int responseCode = conn.getResponseCode();
            log.info("推送助手响应码: {}", responseCode);
            
            // 读取响应
            BufferedReader in = new BufferedReader(new InputStreamReader(
                responseCode >= 200 && responseCode < 300 ? conn.getInputStream() : conn.getErrorStream()
            ));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                response.append(line);
            }
            in.close();
            
            log.info("推送助手响应: {}", response.toString());
            
            if (responseCode < 200 || responseCode >= 300) {
                throw new Exception("推送助手返回错误: " + response.toString());
            }
        } finally {
            conn.disconnect();
        }
    }

    /**
     * 用户登录（用户名密码登录）
     */
    @PostMapping("/login")
    public R<User> login(@RequestBody Map<String, String> map, HttpSession session) {
        log.info("用户登录: {}", map);

        String username = map.get("username");
        String password = map.get("password");
        
        // 参数校验
        if (username == null || username.isEmpty()) {
            return R.error("用户名不能为空");
        }
        if (password == null || password.isEmpty()) {
            return R.error("密码不能为空");
        }

        try {
            // 根据用户名和密码查询用户
            User user = userService.login(username, password);
            
            if (user == null) {
                return R.error("用户名或密码错误");
            }

            // 将用户ID保存到session
            session.setAttribute("user", user.getId());

            log.info("用户登录成功: username={}, userId={}", username, user.getId());
            return R.success(user);
        } catch (Exception e) {
            log.error("登录失败: {}", e.getMessage());
            return R.error(e.getMessage() != null ? e.getMessage() : "登录失败");
        }
    }

    @PostMapping("/loginin")
    public R<User> loginin(@RequestBody Map<String, String> map, HttpSession session) {
        log.info("用户登录: {}", map);

        String phone = map.get("phone");
        String code = map.get("code");

        // 参数校验
        if (phone == null || phone.isEmpty()) {
            return R.error("手机号不能为空");
        }
        if (code == null || code.isEmpty()) {
            return R.error("验证码不能为空");
        }

        try {
            // 从session中获取之前发送的验证码
            String savedCode = (String) session.getAttribute(phone);

            if (savedCode == null) {
                return R.error("验证码已过期，请重新获取");
            }

            // 验证验证码是否正确
            if (!savedCode.equals(code)) {
                return R.error("验证码错误");
            }

            // 验证码正确，根据手机号查询或创建用户
            User user = userService.loginByPhone(phone);

            // 将用户ID保存到session
            session.setAttribute("user", user.getId());

            // 清除session中的验证码（一次性使用）
            session.removeAttribute(phone);

            log.info("用户登录成功: phone={}, userId={}", phone, user.getId());
            return R.success(user);
        } catch (Exception e) {
            log.error("登录失败: {}", e.getMessage());
            return R.error(e.getMessage() != null ? e.getMessage() : "登录失败");
        }
    }
    
    /**
     * 用户注册
     */
    @PostMapping("/register")
    public R<User> register(@RequestBody Map<String, String> map) {
        log.info("用户注册: {}", map);
    
        String username = map.get("username");
        String password = map.get("password");
            
        try {
            // 注册用户
            User user = userService.registerUser(username, password);
            return R.success(user);
        } catch (Exception e) {
            log.error("注册失败: {}", e.getMessage());
            return R.error(e.getMessage() != null ? e.getMessage() : "注册失败");
        }
    }



    /**
     * 用户退出
     */
    @PostMapping("/logout")
    public R<String> logout(HttpSession session) {
        session.removeAttribute("user");
        return R.success("退出成功");
    }

    /**
     * 手机端用户退出登录
     */
    @PostMapping("/loginout")
    public R<String> loginout(HttpSession session) {
        session.removeAttribute("user");
        return R.success("退出成功");
    }

    /**
     * 检查登录状态
     */
    @GetMapping("/checkLogin")
    public R<User> checkLogin(HttpSession session) {
        Long userId = (Long) session.getAttribute("user");
        if (userId != null) {
            // 已登录，返回用户信息
            User user = userService.getUserById(userId);
            return R.success(user);
        } else {
            // 未登录
            return R.error("未登录");
        }
    }
}
