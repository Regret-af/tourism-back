package com.af.tourism.pojo.dto.admin;

import com.af.tourism.common.enums.AttractionCategoryStatus;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 管理端景点分类状态修改请求
 */
@Data
public class AttractionCategoryStatusUpdateDTO {

    @NotNull(message = "status不能为空")
    private AttractionCategoryStatus status;
}
