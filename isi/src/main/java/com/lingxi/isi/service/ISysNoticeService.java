package com.lingxi.isi.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lingxi.isi.common.result.R;
import com.lingxi.isi.models.entity.SysNotice;

import java.util.List;

public interface ISysNoticeService extends IService<SysNotice> {
    
    R<List<SysNotice>> listTop();
}
