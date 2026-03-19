package com.af.tourism.service;

import com.af.tourism.pojo.vo.DiaryFavoriteVO;

public interface DiaryFavoriteService {

    /**
     * 收藏旅行日记
     * @param diaryId 旅行日记 id
     * @param userId 用户 id
     * @return 收藏状态和数量
     */
    DiaryFavoriteVO favoriteDiary(Long diaryId, Long userId);

    /**
     * 取消收藏旅行日记
     * @param diaryId 旅行日记 id
     * @param userId 用户 id
     * @return 收藏状态和数量
     */
    DiaryFavoriteVO unfavoriteDiary(Long diaryId, Long userId);
}
