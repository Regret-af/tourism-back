package com.af.tourism.pojo.vo.app;

import lombok.Data;

/**
 * 景点分类列表响应。
 */
@Data
public class AttractionCategoryVO {

    private Long id;
    private String name;
    private String code;
    private Integer sortOrder;
}
