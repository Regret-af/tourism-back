package com.af.tourism.service.impl;

import com.af.tourism.mapper.DiaryFavoriteMapper;
import com.af.tourism.mapper.DiaryMapper;
import com.af.tourism.pojo.entity.DiaryFavorite;
import com.af.tourism.pojo.entity.TravelDiary;
import com.af.tourism.pojo.vo.DiaryFavoriteVO;
import com.af.tourism.service.DiaryFavoriteService;
import com.af.tourism.service.helper.DiaryCheckService;
import com.af.tourism.service.helper.UserCheckService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 收藏服务实现。
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DiaryFavoriteServiceImpl implements DiaryFavoriteService {

    private final DiaryFavoriteMapper diaryFavoriteMapper;
    private final DiaryMapper diaryMapper;

    private final UserCheckService userCheckService;
    private final DiaryCheckService diaryCheckService;

    /**
     * 收藏旅行日记
     * @param diaryId 旅行日记 id
     * @param userId 用户 id
     * @return 收藏状态和数量
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public DiaryFavoriteVO favoriteDiary(Long diaryId, Long userId) {
        // 1.校验参数是否存在
        TravelDiary diary = diaryCheckService.requirePublicDiary(diaryId);
        userCheckService.requireActiveUser(userId);

        // 2.查看是否已经收藏
        DiaryFavorite existed = diaryFavoriteMapper.selectByDiaryIdAndUserId(diaryId, userId);
        if (existed == null) {
            // 3.未收藏，则插入收藏记录并更新收藏数量
            DiaryFavorite favorite = new DiaryFavorite();
            favorite.setDiaryId(diaryId);
            favorite.setUserId(userId);
            diaryFavoriteMapper.insert(favorite);
            diaryMapper.updateFavoriteCount(diaryId, 1);
            diary = diaryMapper.selectById(diaryId);
            log.info("收藏日记成功，diaryId={}, userId={}", diaryId, userId);
        } else {
            log.info("重复收藏，直接返回当前状态，diaryId={}, userId={}", diaryId, userId);
        }

        return buildFavoriteVO(true, diary.getFavoriteCount());
    }

    /**
     * 取消收藏旅行日记
     * @param diaryId 旅行日记 id
     * @param userId 用户 id
     * @return 收藏状态和数量
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public DiaryFavoriteVO unfavoriteDiary(Long diaryId, Long userId) {
        // 1.校验参数是否存在
        TravelDiary diary = diaryCheckService.requirePublicDiary(diaryId);
        userCheckService.requireActiveUser(userId);

        // 2.查看是否已经收藏
        DiaryFavorite existed = diaryFavoriteMapper.selectByDiaryIdAndUserId(diaryId, userId);
        if (existed != null) {
            // 3.若已收藏，则删除收藏记录，并更新收藏数量
            diaryFavoriteMapper.deleteByDiaryIdAndUserId(diaryId, userId);
            diaryMapper.updateFavoriteCount(diaryId, -1);
            diary = diaryMapper.selectById(diaryId);
            log.info("取消收藏成功，diaryId={}, userId={}", diaryId, userId);
        } else {
            log.info("用户本次取消收藏时为未收藏状态，直接返回当前状态，diaryId={}, userId={}", diaryId, userId);
        }

        return buildFavoriteVO(false, diary.getFavoriteCount());
    }

    /**
     * 构建返回值
     * @param favorited 是否收藏
     * @param favoriteCount 收藏数量
     * @return 返回实体
     */
    private DiaryFavoriteVO buildFavoriteVO(boolean favorited, Integer favoriteCount) {
        DiaryFavoriteVO response = new DiaryFavoriteVO();
        response.setFavorited(favorited);
        response.setFavoriteCount(favoriteCount == null ? 0 : favoriteCount);
        return response;
    }
}
