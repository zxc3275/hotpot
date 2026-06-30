package com.hotpot.controller.client;

import com.hotpot.common.Result;
import com.hotpot.service.PointsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/client/points")
@RequiredArgsConstructor
public class PointsController {
    private final PointsService pointsService;

    @GetMapping("/goods")
    public Result<?> goods() {
        return Result.success(pointsService.getGoods());
    }

    @GetMapping("/records")
    public Result<?> records() {
        return Result.success(pointsService.getRecords());
    }

    @GetMapping("/exchanges")
    public Result<?> exchanges() {
        return Result.success(pointsService.getExchanges());
    }

    @PostMapping("/exchange")
    public Result<?> exchange(@RequestBody Map<String, Object> params) {
        Long goodsId = Long.valueOf(params.get("goodsId").toString());
        Integer quantity = params.get("quantity") != null ? Integer.valueOf(params.get("quantity").toString()) : 1;
        pointsService.exchange(goodsId, quantity);
        return Result.success("兑换成功");
    }
}
