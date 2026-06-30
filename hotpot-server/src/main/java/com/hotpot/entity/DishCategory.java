package com.hotpot.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class DishCategory {
    private Long id;
    private String name;
    private String icon;
    private Integer sort;
    private Integer status;
    private LocalDateTime createTime;
}
