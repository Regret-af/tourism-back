package com.af.tourism.pojo.vo;

import lombok.Data;

/**
 * 点赞响应。
 */
@Data
public class DiaryLikeVO {

    private Boolean liked;
    private Integer likeCount;
}
