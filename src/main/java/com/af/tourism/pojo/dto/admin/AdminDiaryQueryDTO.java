package com.af.tourism.pojo.dto.admin;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.time.LocalDateTime;

/**
 * 管理端日记列表查询参数
 */
@Data
public class AdminDiaryQueryDTO {

    @Min(value = 1, message = "pageNum不能小于1")
    private Integer pageNum = 1;

    @Min(value = 1, message = "pageSize不能小于1")
    @Max(value = 50, message = "pageSize不能大于50")
    private Integer pageSize = 10;

    private String keyword;

    @Min(value = 1, message = "authorId不能小于1")
    private Long authorId;

    private Integer status;

    private Integer contentType;

    private Integer visibility;

    @Min(value = 0, message = "isTop不能小于0")
    @Max(value = 1, message = "isTop不能大于1")
    private Integer isTop;

    @Min(value = 0, message = "isDeleted不能小于0")
    @Max(value = 1, message = "isDeleted不能大于1")
    private Integer isDeleted;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime publishedStart;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime publishedEnd;
}
