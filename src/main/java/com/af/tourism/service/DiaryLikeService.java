package com.af.tourism.service;

import com.af.tourism.pojo.vo.DiaryLikeVO;

public interface DiaryLikeService {

    /**
     * 点赞旅行日记
     * @param diaryId 旅行日记 id
     * @param userId 用户 id
     * @return 点赞状态与数量
     */
    DiaryLikeVO likeDiary(Long diaryId, Long userId);

    /**
     * 取消点赞旅行日记
     * @param diaryId 旅行日记 id
     * @param userId 用户 id
     * @return 点赞状态与数量
     */
    DiaryLikeVO unlikeDiary(Long diaryId, Long userId);
}
