package com.af.tourism.pojo.dto.admin;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * 管理端日记逻辑删除状态修改请求
 */
@Data
public class DiaryDeletedUpdateDTO {

    @NotNull(message = "isDeleted不能为空")
    @Min(value = 0, message = "isDeleted不能小于0")
    @Max(value = 1, message = "isDeleted不能大于1")
    private Integer isDeleted;
}
