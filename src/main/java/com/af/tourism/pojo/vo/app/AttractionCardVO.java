package com.af.tourism.pojo.vo.app;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 景点列表项响应。
 */
@Data
public class AttractionCardVO {

    private Long id;
    private String name;
    private String summary;
    private String coverUrl;
    private String locationText;
    private Integer viewCount;
    private BigDecimal longitude;
    private BigDecimal latitude;
    private LocalDateTime createdAt;
    private AttractionCategorySimpleVO category;
}
