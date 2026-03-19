package com.af.tourism.service.impl;

import com.af.tourism.common.ErrorCode;
import com.af.tourism.exception.BusinessException;
import com.af.tourism.mapper.DiaryFavoriteMapper;
import com.af.tourism.mapper.DiaryMapper;
import com.af.tourism.mapper.UserMapper;
import com.af.tourism.pojo.entity.DiaryFavorite;
import com.af.tourism.pojo.entity.TravelDiary;
import com.af.tourism.pojo.vo.DiaryFavoriteVO;
import com.af.tourism.service.DiaryFavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 收藏服务实现。
 */
@Service
@RequiredArgsConstructor
public class DiaryFavoriteServiceImpl implements DiaryFavoriteService {

    private final DiaryFavoriteMapper diaryFavoriteMapper;
    private final DiaryMapper diaryMapper;
    private final UserMapper userMapper;

    private final UserCheckService userCheckService;

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
        TravelDiary diary = requirePublicDiary(diaryId);
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
        TravelDiary diary = requirePublicDiary(diaryId);
        userCheckService.requireActiveUser(userId);

        // 2.查看是否已经收藏
        DiaryFavorite existed = diaryFavoriteMapper.selectByDiaryIdAndUserId(diaryId, userId);
        if (existed != null) {
            // 3.若已收藏，则删除收藏记录，并更新收藏数量
            diaryFavoriteMapper.deleteByDiaryIdAndUserId(diaryId, userId);
            diaryMapper.updateFavoriteCount(diaryId, -1);
            diary = diaryMapper.selectById(diaryId);
        }
        return buildFavoriteVO(false, diary.getFavoriteCount());
    }

    /**
     * 校验旅行日记是否存在
     * @param diaryId 旅行日记 id
     * @return 旅行日记实体
     */
    private TravelDiary requirePublicDiary(Long diaryId) {
        TravelDiary diary = diaryMapper.selectById(diaryId);
        if (diary == null || diary.getStatus() == null || diary.getStatus() != 1) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "旅行日记不存在");
        }
        return diary;
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
