package com.lingxi.seo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lingxi.seo.models.entity.NginxRequestLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * Nginx 请求日志 Mapper
 */
@Mapper
public interface NginxRequestLogMapper extends BaseMapper<NginxRequestLog> {
}
