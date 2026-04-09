package com.af.tourism.pojo.dto.admin;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * 管理端景点状态修改请求
 */
@Data
public class AdminAttractionStatusUpdateDTO {

    @NotNull(message = "status不能为空")
    @Min(value = 0, message = "status不能小于0")
    @Max(value = 1, message = "status不能大于1")
    private Integer status;
}
