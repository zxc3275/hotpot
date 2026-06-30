package com.hotpot.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Dish {
    private Long id;
    private Long categoryId;
    private String name;
    private String image;
    private String description;
    private Integer price;
    private Integer originalPrice;
    private Integer spicyLevel;
    private Integer status;
    private Integer sales;
    private Integer sort;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
