package com.hotpot.service;

import com.hotpot.entity.CartItem;
import java.util.List;

public interface CartService {
    List<CartItem> getCart();
    void addToCart(Long dishId, Integer quantity);
    void updateQuantity(Long id, Integer quantity);
    void removeItem(Long id);
    void clearCart();
    void removeItems(List<Long> ids);
}
