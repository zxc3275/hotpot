package com.hotpot.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class OrderItem {
    private Long id;
    private Long orderId;
    private Long dishId;
    private String dishName;
    private String dishImage;
    private Integer dishPrice;
    private Integer quantity;
    private Integer subtotal;
    private LocalDateTime createTime;
}
