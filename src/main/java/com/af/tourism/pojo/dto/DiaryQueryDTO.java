package com.af.tourism.pojo.dto;

import cn.hutool.core.util.StrUtil;
import com.af.tourism.service.impl.DiaryServiceImpl;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

/**
 * 笔记信息查询参数实体
 */
@Data
public class DiaryQueryDTO {

    @Min(value = 1, message = "页码不能小于1")
    private Integer page = 1;

    @Min(value = 1, message = "每页条数不能小于1")
    @Max(value = 50, message = "每页条数不能大于50")
    private Integer size = 10;

    private Long userId;
    private Integer featured;
    private String q;
    private String sort;
    private String algo;
    private String scene;
    private SortSpec sortSpec; // 解析后的排序字段

    public void parseParams() {
        this.sortSpec = buildSort(sort);
    }

    private SortSpec buildSort(String sort) {
        String sortField = StrUtil.nullToDefault(sort, "-createdAt");
        boolean desc = sortField.startsWith("-");
        if (desc) {
            sortField = sortField.substring(1);
        }
        String column;
        switch (sortField) {
            case "likeCount":
                column = "d.like_count";
                break;
            case "viewCount":
                column = "d.view_count";
                break;
            default:
                column = "d.created_at";
        }
        return new SortSpec(column, desc ? "DESC" : "ASC");
    }

    // 静态内部类
    @Data
    private static class SortSpec {
        final String column;
        final String order;

        SortSpec(String column, String order) {
            this.column = column;
            this.order = order;
        }
    }
}
