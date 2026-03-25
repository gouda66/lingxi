package com.lingxi.isi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lingxi.isi.models.entity.InterviewAnswer;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 面试回答表 Mapper 接口
 * </p>
 *
 * @author lingxi
 * @since 2026-03-25
 */
@Mapper
public interface InterviewAnswerMapper extends BaseMapper<InterviewAnswer> {

}
