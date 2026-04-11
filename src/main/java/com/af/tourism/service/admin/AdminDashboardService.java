package com.af.tourism.service.admin;

import com.af.tourism.pojo.vo.admin.DashboardOverviewVO;

/**
 * 管理端统计看板服务
 */
public interface AdminDashboardService {

    /**
     * 获取看板总览
     * @return 看板总览
     */
    DashboardOverviewVO getOverview();
}
