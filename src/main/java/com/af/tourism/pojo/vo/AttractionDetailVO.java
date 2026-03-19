package com.af.tourism.pojo.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 景点详情响应。
 */
@Data
public class AttractionDetailVO {

    private Long id;
    private String name;
    private String summary;
    private String description;
    private String coverUrl;
    private String locationText;
    private Integer viewCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private AttractionCategorySimpleVO category;
}
