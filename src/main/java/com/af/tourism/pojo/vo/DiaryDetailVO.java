package com.af.tourism.pojo.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 旅行日记详情。
 */
@Data
public class DiaryDetailVO {

    private Long id;
    private String title;
    private String summary;
    private String coverUrl;
    private String content;
    private DiaryAuthorVO author;
    private Integer viewCount;
    private Integer likeCount;
    private Integer favoriteCount;
    private Integer commentCount;
    private String contentType;
    private Boolean liked;
    private Boolean favorited;
    private LocalDateTime publishedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
