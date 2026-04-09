package com.af.tourism.pojo.dto.admin;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * 管理端景点分类排序修改请求
 */
@Data
public class AttractionCategorySortOrderUpdateDTO {

    @NotNull(message = "sortOrder不能为空")
    @Min(value = 0, message = "sortOrder不能小于0")
    private Integer sortOrder;
}
