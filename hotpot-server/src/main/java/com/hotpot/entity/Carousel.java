package com.hotpot.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Carousel {
    private Long id;
    private String title;
    private String imageUrl;
    private String linkUrl;
    private Integer type;
    private Integer sort;
    private Integer status;
    private LocalDateTime createTime;
}
