package com.af.tourism.pojo.dto.admin;

import com.af.tourism.common.enums.AttractionStatus;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 管理端景点状态修改请求
 */
@Data
public class AdminAttractionStatusUpdateDTO {

    @NotNull(message = "status不能为空")
    private AttractionStatus status;
}
