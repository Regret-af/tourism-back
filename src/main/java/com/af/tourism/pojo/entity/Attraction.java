package com.af.tourism.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 景点实体，对应表 attractions。
 */
@Data
@TableName("attractions")
public class Attraction {
    @TableId
    private Long id;

    private String name;

    private String description;

    private String location;

    @TableField("image_url")
    private String imageUrl;

    /**
     * 底层存储 JSON 字符串，接口层可转数组。
     */
    private String gallery;

    private BigDecimal rating;

    /**
     * 多标签使用逗号分隔。
     */
    private String tags;

    @TableField("view_count")
    private Integer viewCount;

    @TableField("price_level")
    private Integer priceLevel;

    private Integer status;

    @DecimalMin("-90")
    @DecimalMax("90")
    private BigDecimal latitude;

    @DecimalMin("-180")
    @DecimalMax("180")
    private BigDecimal longitude;

    @TableField("created_at")
    private LocalDateTime createdAt;

    @TableField("updated_at")
    private LocalDateTime updatedAt;

}

