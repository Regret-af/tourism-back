package com.af.tourism.pojo.vo.admin;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 管理端景点分类列表项
 */
@Data
public class AttractionCategoryForAdminVO {

    private Long id;

    private String name;

    private String code;

    private Integer sortOrder;

    private Integer status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
