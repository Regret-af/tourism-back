package com.af.tourism.pojo.dto.admin;

import lombok.Data;

import javax.validation.constraints.Pattern;

/**
 * 统计看板范围查询参数
 */
@Data
public class DashboardRangeQueryDTO {

    @Pattern(regexp = "7d|30d", message = "rangeType只能为7d或30d")
    private String rangeType = "7d";
}
