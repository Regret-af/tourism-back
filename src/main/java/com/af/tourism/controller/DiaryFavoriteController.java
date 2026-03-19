package com.af.tourism.controller;

import com.af.tourism.common.ApiResponse;
import com.af.tourism.pojo.vo.DiaryFavoriteVO;
import com.af.tourism.securitylite.AuthContext;
import com.af.tourism.service.DiaryFavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Min;

/**
 * 收藏接口。
 */
@Validated
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class DiaryFavoriteController {

    private final DiaryFavoriteService diaryFavoriteService;

    /**
     * 收藏旅行日记
     * @param diaryId 旅行日记 id
     * @return 收藏状态和数量
     */
    @PostMapping("/travel-diaries/{diaryId}/favorites")
    public ApiResponse<DiaryFavoriteVO> favoriteDiary(@PathVariable("diaryId") @Min(value = 1, message = "diaryId不能小于1") Long diaryId) {
        // 查看是否登录，未登录报错
        Long userId = AuthContext.requireCurrentUserId();
        return ApiResponse.ok(diaryFavoriteService.favoriteDiary(diaryId, userId));
    }

    /**
     * 取消收藏旅行日记
     * @param diaryId 旅行日记 id
     * @return 收藏状态和数量
     */
    @DeleteMapping("/travel-diaries/{diaryId}/favorites")
    public ApiResponse<DiaryFavoriteVO> unfavoriteDiary(@PathVariable("diaryId") @Min(value = 1, message = "diaryId不能小于1") Long diaryId) {
        // 查看是否登录，未登录报错
        Long userId = AuthContext.requireCurrentUserId();
        return ApiResponse.ok(diaryFavoriteService.unfavoriteDiary(diaryId, userId));
    }
}
