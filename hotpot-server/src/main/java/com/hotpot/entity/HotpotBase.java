package com.hotpot.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class HotpotBase {
    private Long id;
    private Long typeId;
    private String name;
    private String image;
    private String description;
    private Integer price;
    private Integer spicyMin;
    private Integer spicyMax;
    private Integer numMin;
    private Integer numMax;
    private String potType;
    private Integer status;
    private Integer sort;
    private LocalDateTime createTime;
}
