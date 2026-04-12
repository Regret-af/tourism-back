package com.af.tourism.pojo.vo.admin;

import lombok.Data;

/**
 * 热门日记排行项
 */
@Data
public class DashboardTopDiaryVO {

    private Long diaryId;

    private String title;

    private Long authorId;

    private String authorNickname;

    private String coverUrl;

    private Integer viewCount;

    private Integer likeCount;

    private Integer favoriteCount;

    private Integer commentCount;
}
