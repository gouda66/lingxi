package com.lingxi.isi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lingxi.isi.models.entity.EmailRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 邮件发送记录表 Mapper 接口
 * </p>
 *
 * @author lingxi
 * @since 2026-03-25
 */
@Mapper
public interface EmailRecordMapper extends BaseMapper<EmailRecord> {

}
