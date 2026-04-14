package com.af.tourism.pojo.dto.admin;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

/**
 * 管理端日记下拉选项查询参数
 */
@Data
public class DiaryOptionQueryDTO {

    private String keyword;

    @Min(value = 1, message = "authorId不能小于1")
    private Long authorId;

    @Min(value = 1, message = "pageSize不能小于1")
    @Max(value = 20, message = "pageSize不能大于20")
    private Integer pageSize = 10;
}
