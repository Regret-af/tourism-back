package com.af.tourism.service.impl.app;

import com.af.tourism.common.enums.NotificationType;
import com.af.tourism.mapper.DiaryFavoriteMapper;
import com.af.tourism.mapper.DiaryMapper;
import com.af.tourism.pojo.dto.app.FavoriteDiaryQueryDTO;
import com.af.tourism.pojo.dto.common.DiaryInteractionNotifyCommand;
import com.af.tourism.pojo.entity.DiaryFavorite;
import com.af.tourism.pojo.entity.TravelDiary;
import com.af.tourism.pojo.vo.app.FavoriteDiaryCardVO;
import com.af.tourism.pojo.vo.app.DiaryFavoriteVO;
import com.af.tourism.pojo.vo.common.PageResponse;
import com.af.tourism.service.app.DiaryFavoriteService;
import com.af.tourism.service.cache.CacheClearSupport;
import com.af.tourism.service.cache.CacheCounterSupport;
import com.af.tourism.service.helper.DiaryCheckService;
import com.af.tourism.service.helper.DiaryInteractionNotificationService;
import com.af.tourism.service.helper.UserCheckService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 收藏服务实现。
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DiaryFavoriteServiceImpl implements DiaryFavoriteService {

    private final DiaryFavoriteMapper diaryFavoriteMapper;
    private final DiaryMapper diaryMapper;

    private final CacheClearSupport cacheClearSupport;
    private final CacheCounterSupport cacheCounterSupport;

    private final UserCheckService userCheckService;
    private final DiaryCheckService diaryCheckService;
    private final DiaryInteractionNotificationService diaryInteractionNotificationService;

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

            // 4.执行旅行日记收藏数量更新操作
            diaryMapper.updateFavoriteCount(diaryId, 1);

            // 5.清除Redis中可能受到影响的缓存
            // 5.1.清除旅行日记详情缓存
            cacheClearSupport.clearDiaryDetail(diaryId);

            // 6.添加通知列表
            diaryInteractionNotificationService.notifyInteraction(DiaryInteractionNotifyCommand.builder()
                    .type(NotificationType.FAVORITE)
                    .triggerUserId(userId)
                    .recipientUserId(diary.getUserId())
                    .relatedDiaryId(diaryId)
                    .build());

            // 7.更新缓存
            cacheCounterSupport.incrementDiaryFavoriteCount(diaryId, 1);
            log.info("收藏日记成功，diaryId={}, userId={}", diaryId, userId);
        } else {
            log.info("重复收藏，直接返回当前状态，diaryId={}, userId={}", diaryId, userId);
        }

        return buildFavoriteVO(true, diary.getFavoriteCount() + 1);
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

            // 4.清除Redis中可能受到影响的缓存
            // 4.1.清除日记详情缓存
            cacheClearSupport.clearDiaryDetail(diaryId);

            // 5.更新缓存
            cacheCounterSupport.incrementDiaryFavoriteCount(diaryId, -1);
            log.info("取消收藏成功，diaryId={}, userId={}", diaryId, userId);
        } else {
            log.info("用户本次取消收藏时为未收藏状态，直接返回当前状态，diaryId={}, userId={}", diaryId, userId);
        }

        return buildFavoriteVO(false, diary.getFavoriteCount() - 1);
    }

    /**
     * 获取我的收藏列表
     * @param userId 用户 id
     * @param queryDTO 分页参数
     * @return 收藏的日记分页列表
     */
    @Override
    public PageResponse<FavoriteDiaryCardVO> listFavoriteDiaries(Long userId, FavoriteDiaryQueryDTO queryDTO) {
        // 1.开启分页查询
        PageHelper.startPage(queryDTO.getPageNum(), queryDTO.getPageSize());

        // 2.查询收藏列表
        List<FavoriteDiaryCardVO> list = diaryFavoriteMapper.selectFavoriteDiaryList(userId, queryDTO);
        PageInfo<FavoriteDiaryCardVO> pageInfo = new PageInfo<>(list);

        // 3.填充返回值
        PageResponse<FavoriteDiaryCardVO> response = new PageResponse<>();
        response.setList(list);
        response.setPageNum(pageInfo.getPageNum());
        response.setPageSize(pageInfo.getPageSize());
        response.setTotal(pageInfo.getTotal());

        return response;
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
