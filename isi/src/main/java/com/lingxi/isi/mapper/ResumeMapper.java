package com.lingxi.isi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lingxi.isi.models.entity.Resume;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 简历表 Mapper 接口
 * </p>
 *
 * @author lingxi
 * @since 2026-03-25
 */
@Mapper
public interface ResumeMapper extends BaseMapper<Resume> {

}
