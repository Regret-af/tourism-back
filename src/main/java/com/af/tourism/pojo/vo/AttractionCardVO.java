package com.af.tourism.pojo.vo;

import lombok.Data;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 景点列表卡片 VO，对标接口文档 AttractionCard。
 */
@Data
public class AttractionCardVO {
    private Long id;
    private String name;
    private String location;
    private String imageUrl;
    private String description;
    private BigDecimal rating;
    private List<String> tags;
    private Integer viewCount;
    private Integer priceLevel;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private LocalDateTime createdAt;
}

