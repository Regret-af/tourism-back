package com.af.tourism.pojo.dto.admin;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.time.LocalDate;

/**
 * 管理端景点列表查询参数
 */
@Data
public class AdminAttractionQueryDTO {

    @Min(value = 1, message = "pageNum不能小于1")
    private Integer pageNum = 1;

    @Min(value = 1, message = "pageSize不能小于1")
    @Max(value = 50, message = "pageSize不能大于50")
    private Integer pageSize = 10;

    private String keyword;

    private Long categoryId;

    @Min(value = 0, message = "status不能小于0")
    @Max(value = 1, message = "status不能大于1")
    private Integer status;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate createdStart;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate createdEnd;
}
