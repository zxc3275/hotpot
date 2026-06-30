package com.hotpot.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Menu {
    private Long id;
    private Long parentId;
    private String name;
    private String path;
    private String component;
    private String icon;
    private Integer type;
    private String permission;
    private Integer sort;
    private Integer status;
    private LocalDateTime createTime;
}
