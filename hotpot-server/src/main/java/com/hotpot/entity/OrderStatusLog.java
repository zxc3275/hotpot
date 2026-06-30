package com.hotpot.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class OrderStatusLog {
    private Long id;
    private Long orderId;
    private Integer fromStatus;
    private Integer toStatus;
    private String remark;
    private Long operatorId;
    private LocalDateTime createTime;
}
