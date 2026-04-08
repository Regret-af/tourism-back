package com.af.tourism.pojo.vo.app;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

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
    private LocalDateTime sourceSyncedAt;
    private String addressDetail;
    private String telephone;
    private String openingHours;
    private BigDecimal longitude;
    private BigDecimal latitude;
    private List<String> telephoneList;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private AttractionCategorySimpleVO category;
}
