package com.af.tourism.pojo.vo.app;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 旅行日记列表项。
 */
@Data
public class DiaryCardVO {

    private Long id;
    private String title;
    private String summary;
    private String coverUrl;
    private UserPublicVO author;
    private Integer viewCount;
    private Integer likeCount;
    private Integer favoriteCount;
    private Integer commentCount;
    private Boolean isTop;
    private Boolean liked;
    private Boolean favorited;
    private LocalDateTime publishedAt;
}
