package com.hotpot.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResult<T> {
    private long total;
    private List<T> records;
    private int pageNum;
    private int pageSize;

    public static <T> PageResult<T> of(long total, List<T> records, int pageNum, int pageSize) {
        return new PageResult<>(total, records, pageNum, pageSize);
    }
}
