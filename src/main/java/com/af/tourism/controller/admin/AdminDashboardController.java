package com.af.tourism.controller.admin;

import com.af.tourism.common.ApiResponse;
import com.af.tourism.pojo.vo.admin.DashboardOverviewVO;
import com.af.tourism.service.admin.AdminDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
