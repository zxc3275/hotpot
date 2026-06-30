package com.hotpot.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ExchangeRecord {
    private Long id;
    private Long userId;
    private Long goodsId;
    private String goodsName;
    private Integer points;
    private Integer quantity;
    private Integer status;
    private String remark;
    private LocalDateTime createTime;
}
