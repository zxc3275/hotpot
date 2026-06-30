package com.hotpot.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PointsRecord {
    private Long id;
    private Long userId;
    private Integer points;
    private Integer type;
    private String description;
    private Long orderId;
    private LocalDateTime createTime;
}
