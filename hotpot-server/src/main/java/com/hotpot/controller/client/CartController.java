package com.hotpot.controller.client;

import com.hotpot.common.Result;
import com.hotpot.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/client/cart")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;

    @GetMapping("/list")
    public Result<?> list() {
        return Result.success(cartService.getCart());
    }

    @PostMapping("/add")
    public Result<?> add(@RequestBody Map<String, Object> params) {
        Long dishId = Long.valueOf(params.get("dishId").toString());
        Integer quantity = params.get("quantity") != null ? Integer.valueOf(params.get("quantity").toString()) : 1;
        cartService.addToCart(dishId, quantity);
        return Result.success("已加入购物车");
    }

    @PutMapping("/update/{id}")
    public Result<?> update(@PathVariable Long id, @RequestBody Map<String, Object> params) {
        cartService.updateQuantity(id, Integer.valueOf(params.get("quantity").toString()));
        return Result.success();
    }

    @DeleteMapping("/remove/{id}")
    public Result<?> remove(@PathVariable Long id) {
        cartService.removeItem(id);
        return Result.success();
    }

    @DeleteMapping("/clear")
    public Result<?> clear() {
        cartService.clearCart();
        return Result.success();
    }

    @PostMapping("/remove-batch")
    public Result<?> removeBatch(@RequestBody Map<String, List<Long>> params) {
        cartService.removeItems(params.get("ids"));
        return Result.success();
    }
}
