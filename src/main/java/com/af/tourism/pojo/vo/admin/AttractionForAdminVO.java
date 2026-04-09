package com.af.tourism.pojo.vo.admin;

import com.af.tourism.pojo.vo.app.AttractionCategorySimpleVO;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 管理端景点列表项
 */
@Data
public class AttractionForAdminVO {

    private Long id;

    private String name;

    private AttractionCategorySimpleVO category;

    private String summary;

    private String coverUrl;

    private String locationText;

    private String addressDetail;

    private String telephone;

    private String openingHours;

    private Integer status;

    private Integer viewCount;

    private LocalDateTime sourceSyncedAt;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
