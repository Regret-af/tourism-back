package com.af.tourism.pojo.dto.admin;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * 管理端景点分类新增请求
 */
@Data
public class AttractionCategoryCreateDTO {

    @NotBlank(message = "name不能为空")
    @Size(max = 50, message = "name长度不能超过50")
    private String name;

    @NotBlank(message = "code不能为空")
    @Size(max = 50, message = "code长度不能超过50")
    private String code;

    @Min(value = 0, message = "sortOrder不能小于0")
    private Integer sortOrder = 0;

    @NotNull(message = "status不能为空")
    @Min(value = 0, message = "status不能小于0")
    @Max(value = 1, message = "status不能大于1")
    private Integer status;
}
