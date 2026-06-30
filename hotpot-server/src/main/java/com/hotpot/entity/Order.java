package com.hotpot.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Order {
    private Long id;
    private String orderNo;
    private Long userId;
    private Long baseId;
    private Integer basePrice;
    private String potType;
    private Integer spicyLevel;
    private Integer numLevel;
    private Integer totalPrice;
    private Integer status;
    private String remark;
    private String tableNo;
    private LocalDateTime payTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
