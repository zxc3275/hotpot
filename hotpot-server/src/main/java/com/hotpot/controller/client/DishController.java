package com.hotpot.controller.client;

import com.hotpot.common.Result;
import com.hotpot.service.DishService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/client/dish")
@RequiredArgsConstructor
public class DishController {
    private final DishService dishService;

    @GetMapping("/categories")
    public Result<?> categories() {
        return Result.success(dishService.getCategories());
    }

    @GetMapping("/list")
    public Result<?> list(@RequestParam(required = false) Long categoryId,
                          @RequestParam(required = false) String keyword) {
        return Result.success(dishService.getDishes(categoryId, keyword));
    }

    @GetMapping("/detail/{id}")
    public Result<?> detail(@PathVariable Long id) {
        return Result.success(dishService.getDishDetail(id));
    }

    @GetMapping("/top")
    public Result<?> top(@RequestParam(defaultValue = "10") int limit) {
        return Result.success(dishService.getTopSales(limit));
    }
}
