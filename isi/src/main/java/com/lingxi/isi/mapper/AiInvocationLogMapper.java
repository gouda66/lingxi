package com.lingxi.isi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lingxi.isi.models.entity.AiInvocationLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * AI 调用日志表 Mapper 接口
 * </p>
 *
 * @author lingxi
 * @since 2026-03-25
 */
@Mapper
public interface AiInvocationLogMapper extends BaseMapper<AiInvocationLog> {

}
