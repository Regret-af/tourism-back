package com.af.tourism.controller;

import com.af.tourism.annotation.OperationLogRecord;
import com.af.tourism.common.ApiResponse;
import com.af.tourism.pojo.dto.DiaryCommentCreateDTO;
import com.af.tourism.pojo.dto.DiaryCommentQueryDTO;
import com.af.tourism.pojo.vo.DiaryCommentCreateVO;
import com.af.tourism.pojo.vo.DiaryCommentVO;
import com.af.tourism.pojo.vo.PageResponse;
import com.af.tourism.securitylite.AuthContext;
import com.af.tourism.service.DiaryCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.Min;

/**
 * 评论接口。
 */
@Validated
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class DiaryCommentController {

    private final DiaryCommentService diaryCommentService;

    /**
     * 查询旅行日记评论列表
     * @param diaryId 旅行日记 id
     * @param queryDTO 旅行日记评论列表查询条件
     * @return 旅行日记评论列表
     */
    @GetMapping("/travel-diaries/{diaryId}/comments")
    public ApiResponse<PageResponse<DiaryCommentVO>> listComments(@PathVariable("diaryId") @Min(value = 1, message = "diaryId不能小于1") Long diaryId,
                                                                  @Valid DiaryCommentQueryDTO queryDTO) {
        return ApiResponse.ok(diaryCommentService.listComments(diaryId, queryDTO));
    }

    /**
     * 发表评论
     * @param diaryId 旅行日记 id
     * @param request 评论请求，内容
     * @return 评论详情内容
     */
    @PostMapping("/travel-diaries/{diaryId}/comments")
    @OperationLogRecord(module = "DIARY", action = "COMMENT", description = "发表评论", bizIdField = "data.id")
    public ApiResponse<DiaryCommentCreateVO> createComment(@PathVariable("diaryId") @Min(value = 1, message = "diaryId不能小于1") Long diaryId,
                                                           @Valid @RequestBody DiaryCommentCreateDTO request) {
        Long userId = AuthContext.requireCurrentUserId();
        return ApiResponse.ok(diaryCommentService.createComment(diaryId, userId, request));
    }
}
