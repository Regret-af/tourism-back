package com.af.tourism.pojo.vo;

import lombok.Data;

import java.util.List;

/**
 * 通用分页响应结构，保持与 API 定稿一致。
 */
@Data
public class PageResponse<T> {

    private Integer pageNum;
    private Integer pageSize;
    private Long total;
    private Integer pages;
    private List<T> list;

    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
        recalculatePages();
    }

    public void setTotal(Long total) {
        this.total = total;
        recalculatePages();
    }

    /**
     * 兼容当前业务代码中的旧 setter，后续业务重构时可移除。
     */
    @Deprecated
    public void setPage(Integer page) {
        this.pageNum = page;
    }

    /**
     * 兼容当前业务代码中的旧 setter，后续业务重构时可移除。
     */
    @Deprecated
    public void setSize(Integer size) {
        setPageSize(size);
    }

    /**
     * 兼容当前业务代码中的旧 setter，分页结构已不再返回该字段。
     */
    @Deprecated
    public void setHasNext(boolean hasNext) {
        // no-op
    }

    private void recalculatePages() {
        if (this.pageSize == null || this.pageSize <= 0 || this.total == null) {
            return;
        }
        this.pages = (int) ((this.total + this.pageSize - 1) / this.pageSize);
    }
}
