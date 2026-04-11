package com.af.tourism.pojo.dto.admin;

import com.af.tourism.common.enums.DiaryDeletedStatus;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 管理端日记逻辑删除状态修改请求
 */
@Data
public class DiaryDeletedUpdateDTO {

    @NotNull(message = "isDeleted不能为空")
    private DiaryDeletedStatus isDeleted;
}
