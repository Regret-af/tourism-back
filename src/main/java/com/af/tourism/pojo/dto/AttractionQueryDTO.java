package com.af.tourism.pojo.dto;

import cn.hutool.core.util.StrUtil;
import com.af.tourism.service.impl.AttractionServiceImpl;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

import javax.validation.constraints.Min;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 景点信息查询参数实体
 */
@Data
@JsonPropertyOrder(alphabetic = true) // 按照字母属性进行序列化
public class AttractionQueryDTO {

    @Min(value = 1, message = "页码不能小于1")
    Integer page = 1;

    @Min(value = 1, message = "每页条数不能小于1")
    Integer size = 10;

    String q;   // 关键词搜索（匹配名称/描述）
    String location;    // 地点
    Integer priceLevel; // 价格等级
    String tags;    // 标签
    String sort;    // 排序字段
    String algo;    // 算法标识
    String scene;   // 场景
    List<String> tagList;   // 解析后的标签
    SortSpec sortSpec; // 解析后的排序字段

    // 进行参数解析
    public void parseParams() {
        this.tagList = parseTags(tags);
        this.sortSpec = buildSort(sort);
    }

    // 解析逗号分隔标签
    private List<String> parseTags(String tags) {
        if (StrUtil.isBlank(tags)) {
            return Collections.emptyList();
        }
        List<String> list = new ArrayList<>();
        for (String tag : StrUtil.split(tags, ',')) {
            if (StrUtil.isNotBlank(tag)) {
                list.add(tag.trim());
            }
        }
        return list;
    }

    // 解析排序字段
    private SortSpec buildSort(String sort) {
        String sortField = StrUtil.nullToDefault(sort, "-createdAt");
        boolean desc = sortField.startsWith("-");
        if (desc) {
            sortField = sortField.substring(1);
        }
        String column;
        switch (sortField) {
            case "viewCount":
                column = "view_count";
                break;
            case "rating":
                column = "rating";
                break;
            case "priceLevel":
                column = "price_level";
                break;
            default:
                column = "created_at";
        }
        return new SortSpec(column, desc ? "DESC" : "ASC");
    }

    // 内部静态类
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
