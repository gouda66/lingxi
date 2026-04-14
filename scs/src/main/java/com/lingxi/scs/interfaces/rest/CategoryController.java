package com.lingxi.scs.interfaces.rest;

import com.lingxi.scs.application.service.CategoryApplicationService;
import com.lingxi.scs.common.context.BaseContext;
import com.lingxi.scs.common.result.R;
import com.lingxi.scs.domain.model.entity.Category;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/category")
@Slf4j
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryApplicationService categoryService;

    @PostMapping
    public R<String> save(@RequestBody Category category) {
        log.info("新增分类：{}", category.getName());
        Long operatorId = BaseContext.getCurrentId();
        categoryService.addCategory(category, operatorId);
        return R.success("新增分类成功");
    }

    @GetMapping("/list")
    public R<List<Category>> list(@RequestParam(required = false) Integer type) {
        log.info("查询分类列表：type={}", type);
        List<Category> categories;
        if (type != null) {
            categories = categoryService.getCategoriesByType(type);
        } else {
            categories = categoryService.getAllCategories();
        }
        return R.success(categories);
    }

    @GetMapping("/page")
    public R<Page<Category>> page(@RequestParam(defaultValue = "1") int page, 
                                   @RequestParam(defaultValue = "10") int pageSize,
                                   @RequestParam(required = false, defaultValue = "") String name) {
        log.info("分页查询分类，页码：{}, 每页大小：{}, 名称：{}", page, pageSize, name);
        Page<Category> categoryPage = categoryService.pageByName(page, pageSize, name);
        return R.success(categoryPage);
    }

    @DeleteMapping
    public R<String> delete(@RequestParam String id) {
        log.info("删除分类：{}", id);
        categoryService.deleteCategory(Long.parseLong(id));
        return R.success("分类信息删除成功");
    }

    @PutMapping
    public R<String> update(@RequestBody Category category) {
        log.info("修改分类：{}", category.getId());
        Long operatorId = BaseContext.getCurrentId();
        categoryService.updateCategory(category, operatorId);
        return R.success("修改分类信息成功");
    }
}
