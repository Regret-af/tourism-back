package com.af.tourism.pojo.dto.admin;

import com.af.tourism.common.enums.DiaryCommentStatus;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.time.LocalDateTime;

/**
 * 管理端评论列表查询参数
 */
@Data
public class AdminDiaryCommentQueryDTO {

    @Min(value = 1, message = "pageNum不能小于1")
    private Integer pageNum = 1;

    @Min(value = 1, message = "pageSize不能小于1")
    @Max(value = 50, message = "pageSize不能大于50")
    private Integer pageSize = 10;

    private String keyword;

    @Min(value = 1, message = "diaryId不能小于1")
    private Long diaryId;

    @Min(value = 1, message = "userId不能小于1")
    private Long userId;

    private DiaryCommentStatus status;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdStart;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdEnd;
}
