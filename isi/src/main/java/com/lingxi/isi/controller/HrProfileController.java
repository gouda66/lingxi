package com.lingxi.isi.controller;

import com.lingxi.isi.service.IHrProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * HR 信息表 前端控制器
 * </p>
 */
@RestController
@RequestMapping("/hr/profile")
public class HrProfileController {

    private final IHrProfileService hrProfileService;

    public HrProfileController(IHrProfileService hrProfileService) {
        this.hrProfileService = hrProfileService;
    }
}
