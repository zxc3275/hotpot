package com.hotpot.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserFavorite {
    private Long id;
    private Long userId;
    private Long dishId;
    private LocalDateTime createTime;
}
