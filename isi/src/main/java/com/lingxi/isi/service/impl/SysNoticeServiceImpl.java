package com.lingxi.isi.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lingxi.isi.common.result.R;
import com.lingxi.isi.mapper.SysNoticeMapper;
import com.lingxi.isi.models.entity.SysNotice;
import com.lingxi.isi.service.ISysNoticeService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SysNoticeServiceImpl extends ServiceImpl<SysNoticeMapper, SysNotice> implements ISysNoticeService {
    
    @Override
    public R<List<SysNotice>> listTop() {
        List<SysNotice> notices = baseMapper.selectTopNotices();
        return R.success(notices);
    }
}
