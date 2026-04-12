package com.af.tourism.pojo.dto.admin;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;

/**
 * 热门日记排行查询参数
 */
@Data
public class DashboardTopDiaryQueryDTO {

    @Pattern(regexp = "7d|30d", message = "rangeType只能为7d或30d")
    private String rangeType = "7d";

    @Min(value = 1, message = "limit不能小于1")
    @Max(value = 50, message = "limit不能大于50")
    private Integer limit = 10;
}
