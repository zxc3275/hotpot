package com.hotpot.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Feedback {
    private Long id;
    private Long userId;
    private String content;
    private String reply;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime replyTime;
}
