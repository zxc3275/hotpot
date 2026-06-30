package com.hotpot.controller.admin;

import com.hotpot.common.Result;
import com.hotpot.entity.PointsGoods;
import com.hotpot.service.PointsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/points")
@RequiredArgsConstructor
public class PointsManagerController {
    private final PointsService pointsService;

    @GetMapping("/goods")
    public Result<?> goods() { return Result.success(pointsService.getGoods()); }

    @PostMapping("/goods/save")
    public Result<?> saveGoods(@RequestBody PointsGoods goods) {
        pointsService.saveGoods(goods);
        return Result.success("保存成功");
    }

    @DeleteMapping("/goods/delete/{id}")
    public Result<?> deleteGoods(@PathVariable Long id) {
        pointsService.deleteGoods(id);
        return Result.success("删除成功");
    }

    @GetMapping("/exchanges")
    public Result<?> exchanges() { return Result.success(pointsService.allExchanges()); }

    @PutMapping("/exchange/{id}")
    public Result<?> handleExchange(@PathVariable Long id, @RequestBody Map<String, Object> params) {
        pointsService.handleExchange(id,
            Integer.valueOf(params.get("status").toString()),
            params.get("remark") != null ? params.get("remark").toString() : "");
        return Result.success("处理成功");
    }
}
