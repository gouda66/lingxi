package com.lingxi.isi.controller;

import com.lingxi.isi.common.result.R;
import com.lingxi.isi.models.entity.SysNotice;
import com.lingxi.isi.service.ISysNoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/system/notice")
public class SysNoticeController {
    
    @Autowired
    private ISysNoticeService noticeService;
    
    /**
     * 查询置顶公告
     */
    @GetMapping("/listTop")
    public R<List<SysNotice>> listTop() {
        return noticeService.listTop();
    }
}
