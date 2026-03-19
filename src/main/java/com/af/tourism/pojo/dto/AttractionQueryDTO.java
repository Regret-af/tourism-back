package com.af.tourism.pojo.dto;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;

/**
 * 景点列表查询参数。
 */
@Data
public class AttractionQueryDTO {

    @Min(value = 1, message = "pageNum不能小于1")
    private Integer pageNum = 1;

    @Min(value = 1, message = "pageSize不能小于1")
    @Max(value = 50, message = "pageSize不能大于50")
    private Integer pageSize = 10;

    private String keyword;

    private Long categoryId;

    @Pattern(regexp = "^(latest|hot)$", message = "sort只支持latest或hot")
    private String sort;
}
