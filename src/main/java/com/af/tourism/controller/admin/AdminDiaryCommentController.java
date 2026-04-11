package com.af.tourism.controller.admin;

import com.af.tourism.annotation.OperationLogRecord;
import com.af.tourism.common.ApiResponse;
import com.af.tourism.common.enums.OperationLogAction;
import com.af.tourism.common.enums.OperationLogModule;
import com.af.tourism.pojo.dto.admin.AdminDiaryCommentQueryDTO;
import com.af.tourism.pojo.dto.admin.DiaryCommentStatusUpdateDTO;
import com.af.tourism.pojo.vo.admin.DiaryCommentForAdminVO;
import com.af.tourism.pojo.vo.common.PageResponse;
import com.af.tourism.service.admin.AdminDiaryCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.Min;

/**
 * 管理端评论接口
 */
@RestController
@Validated
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminDiaryCommentController {

    private final AdminDiaryCommentService adminDiaryCommentService;

    /**
     * 获取评论列表
     * @param queryDTO 查询参数
     * @return 评论分页列表
     */
    @GetMapping("/diary-comments")
    public ApiResponse<PageResponse<DiaryCommentForAdminVO>> listComments(@Valid AdminDiaryCommentQueryDTO queryDTO) {
        return ApiResponse.ok(adminDiaryCommentService.listComments(queryDTO));
    }

    /**
     * 修改评论状态
     * @param id 评论 id
     * @param request 状态修改请求
     * @return 成功信息
     */
    @PatchMapping("/diary-comments/{id}/status")
    @OperationLogRecord(
            module = OperationLogModule.COMMENT,
            action = OperationLogAction.UPDATE_DIARY_COMMENT_STATUS,
            description = "修改评论状态",
            bizIdArgIndex = 0
    )
    public ApiResponse<Void> updateCommentStatus(
            @PathVariable("id") @Min(value = 1, message = "id不能小于1") Long id,
            @Valid @RequestBody DiaryCommentStatusUpdateDTO request) {
        adminDiaryCommentService.updateCommentStatus(id, request.getStatus());
        return ApiResponse.ok();
    }
}
