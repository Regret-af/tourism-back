package com.af.tourism.service.admin;

import com.af.tourism.pojo.dto.admin.DashboardRangeQueryDTO;
import com.af.tourism.pojo.dto.admin.DashboardTopDiaryQueryDTO;
import com.af.tourism.pojo.vo.admin.AttractionCategoryDistributionVO;
import com.af.tourism.pojo.vo.admin.DashboardOverviewVO;
import com.af.tourism.pojo.vo.admin.DashboardTopDiaryVO;
import com.af.tourism.pojo.vo.admin.DashboardTrendPointVO;
import com.af.tourism.pojo.vo.admin.DashboardTrendsVO;

import javax.validation.Valid;
import java.util.List;

/**
 * 管理端统计看板服务
 */
public interface AdminDashboardService {

    /**
     * 获取看板总览
     * @return 看板总览
     */
    DashboardOverviewVO getOverview();

    /**
     * 获取趋势统计
     * @param queryDTO 查询参数
     * @return 趋势统计
     */
    DashboardTrendsVO getTrends(@Valid DashboardRangeQueryDTO queryDTO);

    /**
     * 获取景点分类分布
     * @return 景点分类分布
     */
    List<AttractionCategoryDistributionVO> getAttractionCategoryDistribution();

    /**
     * 获取操作日志活跃趋势
     * @param queryDTO 查询参数
     * @return 操作日志活跃趋势
     */
    List<DashboardTrendPointVO> getOperationLogTrends(@Valid DashboardRangeQueryDTO queryDTO);

    /**
     * 获取热门日记排行
     * @param queryDTO 查询参数
     * @return 热门日记排行
     */
    List<DashboardTopDiaryVO> getTopDiaries(@Valid DashboardTopDiaryQueryDTO queryDTO);
}
