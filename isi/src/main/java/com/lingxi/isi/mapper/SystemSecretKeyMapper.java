package com.lingxi.isi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lingxi.isi.models.entity.SystemSecretKey;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SystemSecretKeyMapper extends BaseMapper<SystemSecretKey> {
    
    @Select("SELECT * FROM system_secret_key WHERE is_active = 1 ORDER BY id LIMIT 4")
    List<SystemSecretKey> selectActiveKeys();
    
    @Select("SELECT * FROM system_secret_key WHERE key_name = #{name}")
    SystemSecretKey selectByName(String name);
}
