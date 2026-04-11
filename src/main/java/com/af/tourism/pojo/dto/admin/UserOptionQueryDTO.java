package com.af.tourism.pojo.dto.admin;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

/**
 * 管理端用户下拉选项查询参数
 */
@Data
public class UserOptionQueryDTO {

    private String keyword;

    @Min(value = 1, message = "pageSize不能小于1")
    @Max(value = 20, message = "pageSize不能大于20")
    private Integer pageSize = 10;
}
