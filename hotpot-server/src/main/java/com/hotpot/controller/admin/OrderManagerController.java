package com.hotpot.controller.admin;

import com.hotpot.common.Result;
import com.hotpot.entity.Order;
import com.hotpot.mapper.OrderMapper;
import com.hotpot.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/order")
@RequiredArgsConstructor
public class OrderManagerController {
    private final OrderService orderService;
    private final OrderMapper orderMapper;

    @GetMapping("/list")
    public Result<?> list(@RequestParam(required = false) Integer status) {
        return Result.success(orderService.adminList(status));
    }

    @GetMapping("/detail/{id}")
    public Result<?> detail(@PathVariable Long id) {
        Order order = orderService.adminDetail(id);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("orderNo", order.getOrderNo());
        result.put("id", order.getId());
        result.put("userId", order.getUserId());
        result.put("baseId", order.getBaseId());
        result.put("basePrice", order.getBasePrice());
        result.put("potType", order.getPotType());
        result.put("spicyLevel", order.getSpicyLevel());
        result.put("numLevel", order.getNumLevel());
        result.put("totalPrice", order.getTotalPrice());
        result.put("status", order.getStatus());
        result.put("remark", order.getRemark());
        result.put("tableNo", order.getTableNo());
        result.put("payTime", order.getPayTime());
        result.put("createTime", order.getCreateTime());
        result.put("updateTime", order.getUpdateTime());
        result.put("items", orderMapper.findItems(id));
        result.put("statusLogs", orderMapper.findStatusLogs(id));
        return Result.success(result);
    }

    @PutMapping("/status/{id}")
    public Result<?> updateStatus(@PathVariable Long id, @RequestBody Map<String, Object> params) {
        orderService.updateStatus(id,
            Integer.valueOf(params.get("status").toString()),
            params.get("remark") != null ? params.get("remark").toString() : "");
        return Result.success("状态更新成功");
    }
}
