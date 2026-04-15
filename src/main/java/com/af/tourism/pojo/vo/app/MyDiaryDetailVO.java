package com.af.tourism.pojo.vo.app;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 我的单篇日记详情
 */
@Data
public class MyDiaryDetailVO {

    private Long id;

    private String title;

    private String summary;

    private String coverUrl;

    private String content;

    private Integer status;

    private Integer isDeleted;

    private Integer contentType;

    private Integer visibility;

    private Integer isTop;

    private Integer viewCount;

    private Integer likeCount;

    private Integer favoriteCount;

    private Integer commentCount;

    private LocalDateTime publishedAt;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
