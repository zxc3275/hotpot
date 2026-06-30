package com.hotpot.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class RechargeRecord {
    private Long id;
    private Long userId;
    private Integer amount;
    private Integer payMethod;
    private Integer status;
    private String tradeNo;
    private LocalDateTime createTime;
}
