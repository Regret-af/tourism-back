package com.af.tourism.pojo.vo.admin;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 管理端日记列表项
 */
@Data
public class DiaryForAdminVO {

    private Long id;

    private String title;

    private String summary;

    private String coverUrl;

    private DiaryAuthorForAdminVO author;

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
