package com.af.tourism.service.app;

import com.af.tourism.pojo.dto.app.FavoriteDiaryQueryDTO;
import com.af.tourism.pojo.vo.app.FavoriteDiaryCardVO;
import com.af.tourism.pojo.vo.app.DiaryFavoriteVO;
import com.af.tourism.pojo.vo.common.PageResponse;

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

    /**
     * 获取我的收藏列表
     * @param userId 用户 id
     * @param queryDTO 分页参数
     * @return 收藏的日记分页列表
     */
    PageResponse<FavoriteDiaryCardVO> listFavoriteDiaries(Long userId, FavoriteDiaryQueryDTO queryDTO);
}
