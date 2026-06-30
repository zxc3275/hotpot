package com.hotpot.controller.admin;

import com.hotpot.common.Result;
import com.hotpot.mapper.OrderMapper;
import com.hotpot.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/admin/dashboard")
@RequiredArgsConstructor
public class DashboardController {
    private final OrderService orderService;
    private final OrderMapper orderMapper;

    @GetMapping("/stats")
    public Result<?> stats() {
        Map<String, Object> data = new LinkedHashMap<>(orderService.getDashboard());
        // 近7天趋势
        data.put("dailySales", orderMapper.dailySales7Days());
        // 状态分布
        data.put("statusDistribution", orderMapper.statusDistribution());
        // 热门菜品 TOP10
        data.put("topDishes", orderMapper.topDishes());
        // 时段分布
        data.put("hourlyDistribution", orderMapper.hourlyDistribution());
        return Result.success(data);
    }
}
