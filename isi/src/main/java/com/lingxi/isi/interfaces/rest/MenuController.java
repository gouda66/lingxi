package com.lingxi.isi.interfaces.rest;

import com.lingxi.isi.application.dto.MenuDTO;
import com.lingxi.isi.application.service.MenuApplicationService;
import com.lingxi.isi.common.result.R;
import com.lingxi.isi.infrastructure.filter.LoginCheckFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 菜单管理接口
 */
@RestController
@RequestMapping
@RequiredArgsConstructor
public class MenuController {

    private final MenuApplicationService menuApplicationService;

    /**
     * 获取路由信息（用于动态生成前端路由）
     */
    @GetMapping("/getRouters")
    public R<List<MenuDTO>> getRouters() {
        // 从 ThreadLocal 获取当前登录用户 ID
        Long userId = LoginCheckFilter.getCurrentUserId();
        
        // 根据用户 ID 获取有权限的菜单
        List<MenuDTO> menus = menuApplicationService.getMenusByUserId(userId);
        
        return R.success(menus);
    }
}
