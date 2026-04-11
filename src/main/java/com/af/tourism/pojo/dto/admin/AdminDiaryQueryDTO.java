package com.af.tourism.pojo.dto.admin;

import com.af.tourism.common.enums.DiaryDeletedStatus;
import com.af.tourism.common.enums.DiaryStatus;
import com.af.tourism.common.enums.DiaryTopStatus;
import com.af.tourism.common.enums.DiaryVisibility;
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

    private DiaryStatus status;

    private Integer contentType;

    private DiaryVisibility visibility;

    private DiaryTopStatus isTop;

    private DiaryDeletedStatus isDeleted;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime publishedStart;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime publishedEnd;
}
