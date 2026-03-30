package com.lingxi.isi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lingxi.isi.models.entity.SysNotice;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SysNoticeMapper extends BaseMapper<SysNotice> {
    
    @Select("SELECT * FROM sys_notice WHERE status = '0' ORDER BY created_at DESC LIMIT 5")
    List<SysNotice> selectTopNotices();
}
