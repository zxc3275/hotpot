package com.hotpot.controller.admin;

import com.hotpot.common.Result;
import com.hotpot.entity.Dish;
import com.hotpot.entity.DishCategory;
import com.hotpot.service.DishService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/dish")
@RequiredArgsConstructor
public class DishManagerController {
    private final DishService dishService;

    @GetMapping("/list")
    public Result<?> list() { return Result.success(dishService.adminList()); }

    @PostMapping("/save")
    public Result<?> save(@RequestBody Dish dish) {
        dishService.saveDish(dish);
        return Result.success("保存成功");
    }

    @DeleteMapping("/delete/{id}")
    public Result<?> delete(@PathVariable Long id) {
        dishService.deleteDish(id);
        return Result.success("删除成功");
    }

    @GetMapping("/categories")
    public Result<?> categories() { return Result.success(dishService.getCategories()); }

    @PostMapping("/category/save")
    public Result<?> saveCategory(@RequestBody DishCategory category) {
        dishService.saveCategory(category);
        return Result.success("保存成功");
    }

    @DeleteMapping("/category/delete/{id}")
    public Result<?> deleteCategory(@PathVariable Long id) {
        dishService.deleteCategory(id);
        return Result.success("删除成功");
    }
}
