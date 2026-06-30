package com.hotpot.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CookingGuide {
    private Long id;
    private String title;
    private String content;
    private String image;
    private String type;
    private Integer status;
    private Integer sort;
    private LocalDateTime createTime;
}
