package com.hotpot.service;

import com.hotpot.entity.Order;
import java.util.List;
import java.util.Map;

public interface OrderService {
    Order createOrder(Map<String, Object> params);
    Order getOrder(Long id);
    List<Order> getMyOrders(Integer status);
    void pay(Long id);
    void cancel(Long id);
    void confirmServed(Long id);
    // Admin
    List<Order> adminList(Integer status);
    Order adminDetail(Long id);
    void updateStatus(Long id, Integer status, String remark);
    Map<String, Object> getDashboard();
}
