package com.lingxi.seo.common.exception;

import com.lingxi.seo.common.result.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 全局异常处理器
 * 位于 common 包：跨层通用，捕获所有层级的异常
 * 
 * @author system
 */
@ControllerAdvice(annotations = {RestController.class, Controller.class})
@ResponseBody
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 处理数据库约束违反异常
     * 主要处理唯一索引冲突等数据库层面的异常
     *
     * @param ex SQL完整性约束违反异常
     * @return 统一响应结果
     */
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public R<String> handleSQLIntegrityConstraintViolation(SQLIntegrityConstraintViolationException ex) {
        log.error("数据库约束违反异常: {}", ex.getMessage());

        if (ex.getMessage().contains("Duplicate entry")) {
            String[] split = ex.getMessage().split(" ");
            String msg = split[2] + "已存在";
            return R.error(msg);
        }

        return R.error("数据库操作失败");
    }

    /**
     * 处理自定义业务异常
     * 捕获业务逻辑层抛出的自定义异常
     *
     * @param ex 自定义异常
     * @return 统一响应结果
     */
    @ExceptionHandler(CustomException.class)
    public R<String> handleCustomException(CustomException ex) {
        log.error("业务异常: {}", ex.getMessage());
        return R.error(ex.getMessage());
    }

    /**
     * 处理所有未捕获的异常
     * 兜底异常处理，防止系统异常信息泄露
     *
     * @param ex 异常对象
     * @return 统一响应结果
     */
    @ExceptionHandler(Exception.class)
    public R<String> handleException(Exception ex) {
        log.error("系统异常: ", ex);
        return R.error("系统繁忙，请稍后重试");
    }
}
