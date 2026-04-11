package com.af.tourism.pojo.vo.admin;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 管理端日记详情
 */
@Data
public class DiaryDetailForAdminVO {

    private Long id;

    private DiaryAuthorForAdminVO author;

    private String title;

    private String summary;

    private String coverUrl;

    private String content;

    private Integer status;

    private Integer isDeleted;

    private String contentType;

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
