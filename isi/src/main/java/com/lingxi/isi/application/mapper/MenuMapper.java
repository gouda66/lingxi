package com.lingxi.isi.application.mapper;

import com.lingxi.isi.application.dto.MenuDTO;
import com.lingxi.isi.domain.model.entity.Menu;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 菜单 Mapper 接口
 */
@Mapper
public interface MenuMapper {

    MenuMapper INSTANCE = Mappers.getMapper(MenuMapper.class);

    @Mappings({
        @Mapping(target = "children", ignore = true)
    })
    MenuDTO toDTO(Menu menu);

    List<MenuDTO> toDTOList(List<Menu> menus);

    Menu toEntity(MenuDTO menuDTO);
}
