package com.af.tourism.pojo.vo.admin;

import lombok.Data;

import java.util.List;

/**
 * 统计看板趋势数据
 */
@Data
public class DashboardTrendsVO {

    private List<DashboardTrendPointVO> newUsers;

    private List<DashboardTrendPointVO> newDiaries;

    private List<DashboardTrendPointVO> newComments;
}
