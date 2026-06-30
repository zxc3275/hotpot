package com.hotpot.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CartItem {
    private Long id;
    private Long userId;
    private Long dishId;
    private String dishName;
    private String dishImage;
    private Integer dishPrice;
    private Integer quantity;
    private LocalDateTime createTime;
}
