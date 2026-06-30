package com.hotpot.controller.client;

import com.hotpot.common.Result;
import com.hotpot.entity.Order;
import com.hotpot.mapper.OrderMapper;
import com.hotpot.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/client/order")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    private final OrderMapper orderMapper;

    @PostMapping("/create")
    public Result<?> create(@RequestBody Map<String, Object> params) {
        return Result.success(orderService.createOrder(params));
    }

    @GetMapping("/list")
    public Result<?> list(@RequestParam(required = false) Integer status) {
        return Result.success(orderService.getMyOrders(status));
    }

    @GetMapping("/detail/{id}")
    public Result<?> detail(@PathVariable Long id) {
        Order order = orderService.getOrder(id);
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
        return Result.success(result);
    }

    @GetMapping("/items/{id}")
    public Result<?> items(@PathVariable Long id) {
        return Result.success(orderMapper.findItems(id));
    }

    @PostMapping("/pay/{id}")
    public Result<?> pay(@PathVariable Long id) {
        orderService.pay(id);
        return Result.success("支付成功");
    }

    @PostMapping("/cancel/{id}")
    public Result<?> cancel(@PathVariable Long id) {
        orderService.cancel(id);
        return Result.success("已取消");
    }

    @PostMapping("/confirm/{id}")
    public Result<?> confirmServed(@PathVariable Long id) {
        orderService.confirmServed(id);
        return Result.success("已确认上菜");
    }
}
