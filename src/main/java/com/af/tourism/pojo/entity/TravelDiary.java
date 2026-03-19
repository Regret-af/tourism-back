package com.af.tourism.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 旅行日记实体。
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

    @TableField("cover_url")
    private String coverUrl;

    private String content;

    private Integer status;

    @TableField("view_count")
    private Integer viewCount;

    @TableField("like_count")
    private Integer likeCount;

    @TableField("favorite_count")
    private Integer favoriteCount;

    @TableField("comment_count")
    private Integer commentCount;

    @TableField("published_at")
    private LocalDateTime publishedAt;

    @TableField("created_at")
    private LocalDateTime createdAt;

    @TableField("updated_at")
    private LocalDateTime updatedAt;
}
