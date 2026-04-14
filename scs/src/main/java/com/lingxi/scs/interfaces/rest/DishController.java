package com.lingxi.scs.interfaces.rest;

import com.lingxi.scs.application.dto.DishDTO;
import com.lingxi.scs.common.context.BaseContext;
import com.lingxi.scs.common.result.R;
import com.lingxi.scs.interfaces.facade.DishFacade;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/dish")
@RequiredArgsConstructor
public class DishController {

    private final DishFacade dishFacade;

    @PostMapping
    public R<String> save(@RequestBody DishDTO dishDTO) {
        log.info("新增菜品：{}", dishDTO.getName());
        Long operatorId = BaseContext.getCurrentId();
        dishFacade.addDish(dishDTO, operatorId);
        return R.success("新增菜品成功");
    }

    @GetMapping("/{id}")
    public R<DishDTO> get(@PathVariable String id) {
        log.info("查询菜品：{}", id);
        DishDTO dish = dishFacade.getDishById(Long.parseLong(id));
        return R.success(dish);
    }

    @PutMapping
    public R<String> update(@RequestBody DishDTO dishDTO) {
        log.info("修改菜品：{}", dishDTO.getId());
        Long operatorId = BaseContext.getCurrentId();
        dishFacade.updateDish(dishDTO, operatorId);
        return R.success("修改菜品成功");
    }

    @GetMapping("/list")
    public R<List<DishDTO>> list(@RequestParam(required = false) Long categoryId) {
        log.info("查询菜品列表：categoryId={}", categoryId);
        List<DishDTO> dishes = dishFacade.getDishList(categoryId);
        return R.success(dishes);
    }

    @GetMapping("/page")
    public R<Page<DishDTO>> page(@RequestParam(defaultValue = "1") int page, 
                                  @RequestParam(defaultValue = "10") int pageSize,
                                  @RequestParam(required = false, defaultValue = "") String name) {
        log.info("分页查询菜品，页码：{}, 每页大小：{}, 名称：{}", page, pageSize, name);
        Page<DishDTO> dishPage = dishFacade.getPageByName(page, pageSize, name);
        return R.success(dishPage);
    }

    @PostMapping("/status/{status}")
    public R<String> updateStatus(@PathVariable Integer status, @RequestParam List<String> ids) {
        log.info("批量修改菜品状态：ids={}, status={}", ids, status);
        Long operatorId = BaseContext.getCurrentId();
        List<Long> longIds = ids.stream().map(Long::parseLong).collect(java.util.stream.Collectors.toList());
        dishFacade.batchUpdateStatus(longIds, status, operatorId);
        return R.success("菜品状态修改成功");
    }

    @DeleteMapping
    public R<String> batchDelete(@RequestParam List<String> ids) {
        log.info("批量删除菜品：ids={}", ids);
        List<Long> longIds = ids.stream().map(Long::parseLong).collect(java.util.stream.Collectors.toList());
        dishFacade.batchDelete(longIds);
        return R.success("批量删除成功");
    }
}
