package com.af.tourism.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 景点实体。
 */
@Data
@TableName("attractions")
public class Attraction {

    @TableId
    private Long id;

    @TableField("category_id")
    private Long categoryId;

    private String name;

    private String summary;

    private String description;

    @TableField("cover_url")
    private String coverUrl;

    @TableField("location_text")
    private String locationText;

    private BigDecimal latitude;

    private BigDecimal longitude;

    private Integer status;

    @TableField("view_count")
    private Integer viewCount;

    private String baiduUid;

    @TableField("sourceSynced_at")
    private LocalDateTime sourceSyncedAt;

    @TableField("address_detail")
    private String addressDetail;

    private String telephone;

    @TableField("opening_hours")
    private String openingHours;

    @TableField("created_at")
    private LocalDateTime createdAt;

    @TableField("updated_at")
    private LocalDateTime updatedAt;
}
