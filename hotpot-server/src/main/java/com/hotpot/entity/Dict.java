package com.hotpot.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Dict {
    private Long id;
    private String dictType;
    private String dictKey;
    private String dictValue;
    private Integer sort;
    private Integer status;
    private String remark;
    private LocalDateTime createTime;
}
