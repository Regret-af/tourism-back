package com.af.tourism.pojo.dto;

import com.af.tourism.common.enums.SortType;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

/**
 * 作者更多创作查询参数。
 */
@Data
public class MoreAuthorDiaryQueryDTO {

    @Min(value = 1, message = "pageNum不能小于1")
    private Integer pageNum = 1;

    @Min(value = 1, message = "pageSize不能小于1")
    @Max(value = 50, message = "pageSize不能大于50")
    private Integer pageSize = 10;

    private SortType sort;

    private Long excludeDiaryId;

    public String getSortCode() {
        return sort == null ? null : sort.getCode();
    }

}
