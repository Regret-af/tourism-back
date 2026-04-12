package com.af.tourism.pojo.vo.admin;

import lombok.Data;

/**
 * 景点分类分布
 */
@Data
public class AttractionCategoryDistributionVO {

    private Long categoryId;

    private String categoryName;

    private Long attractionCount;
}
