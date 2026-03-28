package com.af.tourism.pojo.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 我的收藏列表项。
 */
@Data
public class FavoriteDiaryCardVO {

    private Long id;
    private String title;
    private String summary;
    private String coverUrl;
    private UserPublicVO author;
    private Integer viewCount;
    private Integer likeCount;
    private Integer favoriteCount;
    private Integer commentCount;
    private Boolean liked;
    private Boolean favorited;
    private LocalDateTime publishedAt;
    private Boolean invalid;
}
