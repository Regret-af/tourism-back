package com.af.tourism.pojo.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MyDiaryCardVO {

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
    private Boolean isTop;
    private Boolean liked;
    private Boolean favorited;
    private LocalDateTime publishedAt;

}
