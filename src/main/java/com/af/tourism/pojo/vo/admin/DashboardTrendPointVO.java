package com.af.tourism.pojo.vo.admin;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 统计趋势点
 */
@Data
@AllArgsConstructor
public class DashboardTrendPointVO {

    private String date;

    private Long count;
}
