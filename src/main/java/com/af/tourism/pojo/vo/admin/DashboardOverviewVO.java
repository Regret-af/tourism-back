package com.af.tourism.pojo.vo.admin;

import lombok.Data;

/**
 * 管理端看板总览
 */
@Data
public class DashboardOverviewVO {

    private Long userTotal;

    private Long userEnabledCount;

    private Long userDisabledCount;

    private Long attractionTotal;

    private Long attractionOnlineCount;

    private Long attractionOfflineCount;

    private Long diaryTotal;

    private Long diaryOnlineCount;

    private Long diaryOfflineCount;

    private Long diaryPendingReviewCount;

    private Long diaryRejectedCount;

    private Long diaryDeletedCount;
}
