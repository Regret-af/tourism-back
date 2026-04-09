package com.af.tourism.controller.app;

import com.af.tourism.annotation.OperationLogRecord;
import com.af.tourism.common.ApiResponse;
import com.af.tourism.common.enums.OperationLogAction;
import com.af.tourism.common.enums.OperationLogModule;
import com.af.tourism.pojo.vo.app.DiaryLikeVO;
import com.af.tourism.securitylite.AuthContext;
import com.af.tourism.service.app.DiaryLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Min;

/**
 * 点赞接口。
 */
@Validated
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class DiaryLikeController {

    private final DiaryLikeService diaryLikeService;

    /**
     * 点赞旅行日记
     * @param diaryId 旅行日记 id
     * @return 点赞状态与数量
     */
    @PostMapping("/travel-diaries/{diaryId}/likes")
    @OperationLogRecord(module = OperationLogModule.DIARY, action = OperationLogAction.LIKE, description = "点赞旅行日记", bizIdArgIndex = 0)
    public ApiResponse<DiaryLikeVO> likeDiary(@PathVariable("diaryId") @Min(value = 1, message = "diaryId不能小于1") Long diaryId) {
        // 查看是否登录，未登录报错
        Long userId = AuthContext.requireCurrentUserId();
        return ApiResponse.ok(diaryLikeService.likeDiary(diaryId, userId));
    }

    /**
     * 取消点赞旅行日记
     * @param diaryId 旅行日记 id
     * @return 点赞状态与数量
     */
    @DeleteMapping("/travel-diaries/{diaryId}/likes")
    @OperationLogRecord(module = OperationLogModule.DIARY, action = OperationLogAction.UNLIKE, description = "取消点赞旅行日记", bizIdArgIndex = 0)
    public ApiResponse<DiaryLikeVO> unlikeDiary(@PathVariable("diaryId") @Min(value = 1, message = "diaryId不能小于1") Long diaryId) {
        // 查看是否登录，未登录报错
        Long userId = AuthContext.requireCurrentUserId();
        return ApiResponse.ok(diaryLikeService.unlikeDiary(diaryId, userId));
    }
}
