package com.hotpot.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PointsGoods {
    private Long id;
    private String name;
    private String image;
    private String description;
    private Integer points;
    private Integer stock;
    private Integer status;
    private Integer sort;
    private LocalDateTime createTime;
}
