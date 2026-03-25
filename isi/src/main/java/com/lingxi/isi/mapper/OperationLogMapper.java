package com.lingxi.isi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lingxi.isi.models.entity.OperationLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 操作日志表 Mapper 接口
 * </p>
 *
 * @author lingxi
 * @since 2026-03-25
 */
@Mapper
public interface OperationLogMapper extends BaseMapper<OperationLog> {

}
