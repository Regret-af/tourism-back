package com.af.tourism.controller;

import com.af.tourism.common.ApiResponse;
import com.af.tourism.pojo.vo.DiaryLikeVO;
import com.af.tourism.securitylite.AuthContext;
import com.af.tourism.service.DiaryLikeService;
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
    public ApiResponse<DiaryLikeVO> unlikeDiary(@PathVariable("diaryId") @Min(value = 1, message = "diaryId不能小于1") Long diaryId) {
        // 查看是否登录，未登录报错
        Long userId = AuthContext.requireCurrentUserId();
        return ApiResponse.ok(diaryLikeService.unlikeDiary(diaryId, userId));
    }
}
