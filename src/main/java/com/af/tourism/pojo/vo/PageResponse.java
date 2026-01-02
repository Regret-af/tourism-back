package com.af.tourism.pojo.vo;

import lombok.Data;

import java.util.List;

/**
 * 通用分页响应包装，遵循 code/message/data 下的 data 结构。
 */
@Data
public class PageResponse<T> {
    private List<T> list;
    private int page;
    private int size;
    private long total;
    private boolean hasNext;
}

