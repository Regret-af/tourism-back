package com.af.tourism.pojo.dto.admin;

import com.af.tourism.common.enums.DiaryStatus;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 管理端日记状态修改请求
 */
@Data
public class DiaryStatusUpdateDTO {

    @NotNull(message = "status不能为空")
    private DiaryStatus status;
}
