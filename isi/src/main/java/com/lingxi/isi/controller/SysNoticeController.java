package com.lingxi.isi.controller;

import com.lingxi.isi.common.result.R;
import com.lingxi.isi.models.entity.SysNotice;
import com.lingxi.isi.service.ISysNoticeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/system/notice")
public class SysNoticeController {
    
    private final ISysNoticeService noticeService;

    public SysNoticeController(ISysNoticeService noticeService) {
        this.noticeService = noticeService;
    }
    
    /**
     * 查询置顶公告
     */
    @GetMapping("/listTop")
    public R<List<SysNotice>> listTop() {
        return noticeService.listTop();
    }
}
