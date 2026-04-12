package com.af.tourism.controller.admin;

import com.af.tourism.common.ApiResponse;
import com.af.tourism.pojo.dto.admin.DashboardRangeQueryDTO;
import com.af.tourism.pojo.vo.admin.AttractionCategoryDistributionVO;
import com.af.tourism.pojo.vo.admin.DashboardOverviewVO;
import com.af.tourism.pojo.vo.admin.DashboardTrendsVO;
import com.af.tourism.service.admin.AdminDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * 管理端统计看板接口
 */
@RestController
@RequestMapping("/api/v1/admin/dashboard")
@RequiredArgsConstructor
public class AdminDashboardController {

    private final AdminDashboardService adminDashboardService;

    /**
     * 获取看板总览
     * @return 看板总览
     */
    @GetMapping("/overview")
    public ApiResponse<DashboardOverviewVO> getOverview() {
        return ApiResponse.ok(adminDashboardService.getOverview());
    }

    /**
     * 获取趋势统计
     * @param queryDTO 查询参数
     * @return 趋势统计
     */
    @GetMapping("/trends")
    public ApiResponse<DashboardTrendsVO> getTrends(@Valid DashboardRangeQueryDTO queryDTO) {
        return ApiResponse.ok(adminDashboardService.getTrends(queryDTO));
    }

    /**
     * 获取景点分类分布
     * @return 景点分类分布
     */
    @GetMapping("/attraction-category-distribution")
    public ApiResponse<List<AttractionCategoryDistributionVO>> getAttractionCategoryDistribution() {
        return ApiResponse.ok(adminDashboardService.getAttractionCategoryDistribution());
    }
}
