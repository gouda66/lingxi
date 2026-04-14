package com.lingxi.scs.interfaces.rest;

import com.lingxi.scs.application.command.SetmealCommand;
import com.lingxi.scs.application.service.SetmealApplicationService;
import com.lingxi.scs.common.context.BaseContext;
import com.lingxi.scs.common.result.R;
import com.lingxi.scs.domain.model.entity.Setmeal;
import com.lingxi.scs.domain.model.entity.SetmealDish;
import com.lingxi.scs.interfaces.facade.SetmealFacade;
import com.lingxi.scs.application.dto.vo.SetmealDetailVO;
import com.lingxi.scs.application.dto.vo.SetmealListVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 套餐管理
 *
 * @author system
 */
@Slf4j
@RestController
@RequestMapping("/setmeal")
@RequiredArgsConstructor
public class SetmealController {

    private final SetmealApplicationService setmealService;
    private final SetmealFacade setmealFacade;

    /**
     * 新增套餐
     */
    @PostMapping
    public R<String> save(@RequestBody SetmealCommand command) {
        Long operatorId = BaseContext.getCurrentId();
        setmealService.addSetmealWithDishes(command, command.getSetmealDishes(), operatorId);
        return R.success("新增套餐成功");
    }

    /**
     * 修改套餐
     */
    @PutMapping
    public R<String> update(@RequestBody SetmealCommand command) {
        Long operatorId = BaseContext.getCurrentId();
        setmealService.updateSetmealWithDishes(command, command.getSetmealDishes(), operatorId);
        return R.success("修改套餐成功");
    }

    /**
     * 删除套餐
     */
    @DeleteMapping
    public R<String> delete(@RequestParam List<String> ids) {
        log.info("删除套餐: {}", ids);
        for (String id : ids) {
            setmealService.deleteSetmeal(Long.parseLong(id));
        }
        return R.success("套餐数据删除成功");
    }

    /**
     * 根据ID查询套餐（包含分类名和菜品列表）
     */
    @GetMapping("/{id}")
    public R<SetmealDetailVO> get(@PathVariable String id) {
        log.info("查询套餐: {}", id);
        SetmealDetailVO detail = setmealFacade.getSetmealDetail(Long.parseLong(id));
        return R.success(detail);
    }

    /**
     * 根据条件查询套餐列表（包含分类名）
     */
    @GetMapping("/list")
    public R<List<SetmealListVO>> list(@RequestParam(required = false) Long categoryId) {
        log.info("查询套餐列表：categoryId={}", categoryId);
        List<SetmealListVO> setmeals = setmealFacade.getSetmealList(categoryId);
        return R.success(setmeals);
    }

    /**
     * 根据名称分页查询套餐
     */
    @GetMapping("/page")
    public R<Page<SetmealListVO>> page(@RequestParam(defaultValue = "1") int page, 
                                        @RequestParam(defaultValue = "10") int pageSize,
                                        @RequestParam(required = false, defaultValue = "") String name) {
        log.info("分页查询套餐，页码：{}, 每页大小：{}, 名称：{}", page, pageSize, name);
        Page<SetmealListVO> setmealPage = setmealFacade.getPageByName(page, pageSize, name);
        return R.success(setmealPage);
    }

    /**
     * 查询套餐菜品
     */
    @GetMapping("/{id}/dishes")
    public R<List<SetmealDish>> getSetmealDishes(@PathVariable String id) {
        log.info("查询套餐菜品: {}", id);
        List<SetmealDish> dishes = setmealService.getSetmealDishes(Long.parseLong(id));
        return R.success(dishes);
    }

    /**
     * 启用/禁用套餐
     */
    @PostMapping("/status/{status}")
    public R<String> updateStatus(@PathVariable Integer status, @RequestParam List<String> ids) {
        log.info("修改套餐状态: id={}, status={}", ids, status);
        Long operatorId = BaseContext.getCurrentId();
        List<Long> longIds = ids.stream().map(Long::parseLong).collect(java.util.stream.Collectors.toList());
        setmealService.updateStatus(longIds, status, operatorId);
        return R.success("套餐状态修改成功");
    }
}
