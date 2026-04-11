package com.af.tourism.pojo.dto.admin;

import com.af.tourism.common.enums.DiaryCommentStatus;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 管理端评论状态修改请求
 */
@Data
public class DiaryCommentStatusUpdateDTO {

    @NotNull(message = "status不能为空")
    private DiaryCommentStatus status;
}
