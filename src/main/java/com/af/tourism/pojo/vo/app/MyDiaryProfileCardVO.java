package com.af.tourism.pojo.vo.app;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 我的主页日记信息
 */
@Data
public class MyDiaryProfileCardVO {

    private Long id;
    private String title;
    private String summary;
    private String coverUrl;
    private UserPublicVO author;
    private Integer status;
    private Integer viewCount;
    private Integer likeCount;
    private Integer favoriteCount;
    private Integer commentCount;
    private Integer visibility;
    private Boolean isTop;
    private Boolean liked;
    private Boolean favorited;
    private LocalDateTime publishedAt;

}
