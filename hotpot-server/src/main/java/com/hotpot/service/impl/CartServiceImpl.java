package com.hotpot.service.impl;

import com.hotpot.common.BusinessException;
import com.hotpot.entity.CartItem;
import com.hotpot.entity.Dish;
import com.hotpot.entity.User;
import com.hotpot.mapper.CartMapper;
import com.hotpot.mapper.DishMapper;
import com.hotpot.mapper.UserMapper;
import com.hotpot.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {
    private final CartMapper cartMapper;
    private final DishMapper dishMapper;
    private final UserMapper userMapper;

    private Long getUserId() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User u = userMapper.findByUsername(username);
        return u.getId();
    }

    @Override
    public List<CartItem> getCart() { return cartMapper.findByUserId(getUserId()); }

    @Override
    public void addToCart(Long dishId, Integer quantity) {
        Dish dish = dishMapper.findById(dishId);
        if (dish == null || dish.getStatus() != 1) throw new BusinessException("菜品不存在或已下架");
        Long userId = getUserId();
        CartItem exist = cartMapper.findByUserAndDish(userId, dishId);
        if (exist != null) {
            cartMapper.updateQuantity(exist.getId(), exist.getQuantity() + quantity);
        } else {
            CartItem item = new CartItem();
            item.setUserId(userId);
            item.setDishId(dishId);
            item.setDishName(dish.getName());
            item.setDishImage(dish.getImage());
            item.setDishPrice(dish.getPrice());
            item.setQuantity(quantity);
            cartMapper.insert(item);
        }
    }

    @Override
    public void updateQuantity(Long id, Integer quantity) {
        if (quantity <= 0) { cartMapper.delete(id); return; }
        cartMapper.updateQuantity(id, quantity);
    }

    @Override
    public void removeItem(Long id) { cartMapper.delete(id); }

    @Override
    public void clearCart() { cartMapper.clearByUser(getUserId()); }

    @Override
    public void removeItems(List<Long> ids) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < ids.size(); i++) {
            if (i > 0) sb.append(",");
            sb.append(ids.get(i));
        }
        cartMapper.deleteByIds(getUserId(), sb.toString());
    }
}
