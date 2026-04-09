package com.af.tourism.pojo.vo.admin;

import com.af.tourism.pojo.vo.app.AttractionCategorySimpleVO;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 管理端景点详情
 */
@Data
public class AttractionDetailForAdminVO {

    private Long id;

    private AttractionCategorySimpleVO category;

    private String name;

    private String summary;

    private String description;

    private String coverUrl;

    private String locationText;

    private String addressDetail;

    private String telephone;

    private String openingHours;

    private String baiduUid;

    private LocalDateTime sourceSyncedAt;

    private BigDecimal longitude;

    private BigDecimal latitude;

    private Integer status;

    private Integer viewCount;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
