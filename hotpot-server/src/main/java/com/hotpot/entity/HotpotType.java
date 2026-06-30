package com.hotpot.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class HotpotType {
    private Long id;
    private String name;
    private String description;
    private String image;
    private Integer status;
    private Integer sort;
    private LocalDateTime createTime;
}
