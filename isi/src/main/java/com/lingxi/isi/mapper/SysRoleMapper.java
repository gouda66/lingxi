package com.lingxi.isi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lingxi.isi.models.entity.SysRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 用户和角色关联表 Mapper 接口
 * </p>
 *
 * @author lingxi
 * @since 2026-04-01
 */
@Mapper
public interface SysRoleMapper extends BaseMapper<SysRole> {

}
