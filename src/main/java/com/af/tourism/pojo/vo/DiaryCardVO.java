package com.af.tourism.pojo.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 日记列表卡片 VO。
 */
@Data
public class DiaryCardVO {
    private Long id;
    private UserPublicVO user;
    private String title;
    private String summary;
    private String coverImage;
    private Integer isFeatured;
    private Integer likeCount;
    private Integer viewCount;
    private Integer collectCount;
    private Integer commentCount;
    private LocalDateTime createdAt;
}

