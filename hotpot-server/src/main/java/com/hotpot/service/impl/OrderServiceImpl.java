package com.hotpot.service.impl;

import com.hotpot.common.BusinessException;
import com.hotpot.entity.*;
import com.hotpot.mapper.*;
import com.hotpot.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderMapper orderMapper;
    private final CartMapper cartMapper;
    private final UserMapper userMapper;
    private final DishMapper dishMapper;
    private final PointsMapper pointsMapper;
    private final HotpotBaseMapper baseMapper;
    private final UserFavoriteMapper favMapper;
    private final FeedbackMapper feedbackMapper;
    private final RechargeMapper rechargeMapper;

    private Long getUserId() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userMapper.findByUsername(username).getId();
    }

    @Override
    @Transactional
    public Order createOrder(Map<String, Object> params) {
        Long userId = getUserId();
        List<CartItem> cartItems = cartMapper.findByUserId(userId);
        if (cartItems.isEmpty()) throw new BusinessException("购物车为空");

        Long baseId = params.get("baseId") != null ? Long.valueOf(params.get("baseId").toString()) : null;
        Integer spicyLevel = params.get("spicyLevel") != null ? Integer.valueOf(params.get("spicyLevel").toString()) : 2;
        Integer numLevel = params.get("numLevel") != null ? Integer.valueOf(params.get("numLevel").toString()) : 2;
        String potType = params.get("potType") != null ? params.get("potType").toString() : "single";
        String remark = params.get("remark") != null ? params.get("remark").toString() : "";
        String tableNo = params.get("tableNo") != null ? params.get("tableNo").toString() : "";

        Integer basePrice = 0;
        if (baseId != null) {
            HotpotBase base = baseMapper.findById(baseId);
            if (base != null) basePrice = base.getPrice();
        }

        int totalPrice = basePrice;
        for (CartItem item : cartItems) totalPrice += item.getDishPrice() * item.getQuantity();

        User user = userMapper.findById(userId);
        if (user.getBalance() < totalPrice) throw new BusinessException("余额不足，请先充值");

        String orderNo = "HP" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
                + String.format("%04d", new Random().nextInt(10000));

        Order order = new Order();
        order.setOrderNo(orderNo); order.setUserId(userId); order.setBaseId(baseId);
        order.setBasePrice(basePrice); order.setPotType(potType);
        order.setSpicyLevel(spicyLevel); order.setNumLevel(numLevel);
        order.setTotalPrice(totalPrice); order.setStatus(0);
        order.setRemark(remark); order.setTableNo(tableNo);
        orderMapper.insert(order);

        for (CartItem item : cartItems) {
            OrderItem oi = new OrderItem();
            oi.setOrderId(order.getId()); oi.setDishId(item.getDishId());
            oi.setDishName(item.getDishName()); oi.setDishImage(item.getDishImage());
            oi.setDishPrice(item.getDishPrice()); oi.setQuantity(item.getQuantity());
            oi.setSubtotal(item.getDishPrice() * item.getQuantity());
            orderMapper.insertItem(oi);
            dishMapper.addSales(item.getDishId(), item.getQuantity());
        }

        userMapper.addBalance(user.getId(), -totalPrice);
        cartMapper.clearByUser(userId);

        userMapper.addPoints(userId, totalPrice);
        PointsRecord pr = new PointsRecord();
        pr.setUserId(userId); pr.setPoints(totalPrice); pr.setType(1);
        pr.setDescription("消费赠送积分"); pr.setOrderId(order.getId());
        pointsMapper.insertRecord(pr);

        return order;
    }

    @Override
    public Order getOrder(Long id) { return orderMapper.findById(id); }

    @Override
    public List<Order> getMyOrders(Integer status) {
        if (status != null) return orderMapper.findByStatus(status);
        return orderMapper.findByUserId(getUserId());
    }

    @Override
    public void pay(Long id) { orderMapper.pay(id, 1); }

    @Override
    public void cancel(Long id) {
        Order order = orderMapper.findById(id);
        if (order != null && order.getStatus() == 0) {
            orderMapper.updateStatus(id, -1);
            userMapper.addBalance(order.getUserId(), order.getTotalPrice());
        }
    }

    @Override
    public void confirmServed(Long id) { orderMapper.updateStatus(id, 4); }

    @Override
    public List<Order> adminList(Integer status) {
        if (status != null) return orderMapper.findByStatus(status);
        return orderMapper.findAll();
    }

    @Override
    public Order adminDetail(Long id) { return orderMapper.findById(id); }

    @Override
    public void updateStatus(Long id, Integer status, String remark) {
        Order order = orderMapper.findById(id);
        if (order == null) throw new BusinessException("订单不存在");
        OrderStatusLog log = new OrderStatusLog();
        log.setOrderId(id); log.setFromStatus(order.getStatus());
        log.setToStatus(status); log.setRemark(remark); log.setOperatorId(0L);
        orderMapper.insertStatusLog(log);
        orderMapper.updateStatus(id, status);
    }

    @Override
    public Map<String, Object> getDashboard() {
        Map<String, Object> data = new HashMap<>();
        data.put("totalOrders", orderMapper.countAll());
        data.put("todayOrders", orderMapper.countToday());
        data.put("totalSales", orderMapper.totalSales());
        data.put("todaySales", orderMapper.todaySales());
        for (int i = 0; i <= 4; i++) {
            data.put("statusCount" + i, orderMapper.countByStatus(i));
        }
        return data;
    }
}
