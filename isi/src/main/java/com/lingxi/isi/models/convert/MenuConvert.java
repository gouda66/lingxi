package com.lingxi.isi.models.convert;

import com.lingxi.isi.models.dto.MenuDTO;
import com.lingxi.isi.models.entity.SysMenu;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface MenuConvert {
    MenuConvert INSTANCE = Mappers.getMapper(MenuConvert.class);

    /**
     * 将 Entity 转换为 DTO
     */
    @Mapping(target = "id", source = "menuId")
    @Mapping(target = "name", source = "menuName")
    @Mapping(target = "label", source = "menuName")
    @Mapping(target = "parentId", source = "parentId")
    @Mapping(target = "orderNum", source = "orderNum")
    @Mapping(target = "path", source = "path")
    @Mapping(target = "component", source = "component")
    @Mapping(target = "icon", source = "icon")
    @Mapping(target = "title", source = "menuName")
    @Mapping(target = "permissions", expression = "java(parsePermissions(menu.getPerms()))")
    @Mapping(target = "visible", source = "visible")
    @Mapping(target = "hidden", expression = "java(menu.getVisible() == null || !menu.getVisible())")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "children", ignore = true)
    MenuDTO convertToDTO(SysMenu menu);



    /**
     * 解析权限字符串
     */
    default List<String> parsePermissions(String perms) {
        if (perms == null || perms.isEmpty()) {
            return null;
        }
        return java.util.Arrays.asList(perms.split(","));
    }
}
