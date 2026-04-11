package com.lingxi.isi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lingxi.isi.models.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * <p>
 * 用户表 Mapper 接口
 * </p>
 *
 * @author lingxi
 * @since 2026-03-25
 */
@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {


}
