package com.af.tourism.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 日记表实体，对应 travel_diaries。
 */
@Data
@TableName("travel_diaries")
public class TravelDiary {
    @TableId
    private Long id;
    @TableField("user_id")
    private Long userId;
    private String title;
    private String summary;
    @TableField("cover_image")
    private String coverImage;
    private String content;
    @TableField("is_featured")
    private Integer isFeatured;
    private Integer status;
    @TableField("like_count")
    private Integer likeCount;
    @TableField("view_count")
    private Integer viewCount;
    @TableField("collect_count")
    private Integer collectCount;
    @TableField("comment_count")
    private Integer commentCount;
    @TableField("created_at")
    private LocalDateTime createdAt;
    @TableField("updated_at")
    private LocalDateTime updatedAt;
}

