package com.af.tourism.pojo.vo.admin;

import lombok.Data;

/**
 * 管理端景点分类统计信息
 */
@Data
public class AttractionCategoryStatsForAdminVO {

    private Long categoryId;

    private Long attractionCount;
}
