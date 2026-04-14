package com.lingxi.isi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lingxi.isi.models.entity.SysMenu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SysMenuMapper extends BaseMapper<SysMenu> {
    
    @Select("SELECT * FROM sys_menu WHERE status = '0' ORDER BY parent_id, order_num")
    List<SysMenu> selectMenuTree();
    
    @Select("SELECT * FROM sys_menu WHERE menu_id IN (SELECT menu_id FROM sys_role_menu WHERE role_id = #{roleId})")
    List<SysMenu> selectMenuIdsByRoleId(@Param("roleId") Long roleId);
}
