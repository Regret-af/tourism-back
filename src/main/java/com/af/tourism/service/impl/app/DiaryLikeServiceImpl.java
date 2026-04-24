package com.af.tourism.service.impl.app;

import com.af.tourism.common.enums.NotificationType;
import com.af.tourism.mapper.DiaryLikeMapper;
import com.af.tourism.mapper.DiaryMapper;
import com.af.tourism.pojo.dto.common.DiaryInteractionNotifyCommand;
import com.af.tourism.pojo.entity.DiaryLike;
import com.af.tourism.pojo.entity.TravelDiary;
import com.af.tourism.pojo.vo.app.DiaryLikeVO;
import com.af.tourism.service.app.DiaryLikeService;
import com.af.tourism.service.cache.CacheCounterSupport;
import com.af.tourism.service.helper.DiaryCheckService;
import com.af.tourism.service.helper.DiaryInteractionNotificationPublishService;
import com.af.tourism.service.helper.UserCheckService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 点赞服务实现。
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DiaryLikeServiceImpl implements DiaryLikeService {

    private final DiaryLikeMapper diaryLikeMapper;
    private final DiaryMapper diaryMapper;

    private final CacheCounterSupport cacheCounterSupport;

    private final UserCheckService userCheckService;
    private final DiaryCheckService diaryCheckService;
    private final DiaryInteractionNotificationPublishService diaryInteractionNotificationPublishService;

    /**
     * 点赞旅行日记
     * @param diaryId 旅行日记 id
     * @param userId 用户 id
     * @return 点赞状态与数量
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public DiaryLikeVO likeDiary(Long diaryId, Long userId) {
        // 1.校验参数是否存在
        TravelDiary diary = diaryCheckService.requirePublicDiary(diaryId);
        userCheckService.requireActiveUser(userId);

        // 2.判断是否已经点过赞
        DiaryLike existed = diaryLikeMapper.selectByDiaryIdAndUserId(diaryId, userId);
        if (existed == null) {
            // 3.未点赞，则插入点赞记录并更新点赞数量
            DiaryLike like = new DiaryLike();
            like.setDiaryId(diaryId);
            like.setUserId(userId);
            diaryLikeMapper.insert(like);

            // 4.执行旅行日记点赞数量更新操作
            diaryMapper.updateLikeCount(diaryId, 1);

            // 5.清除Redis中可能受到影响的缓存
            // 5.1.清除日记详情缓存

            // 6.添加通知列表
            diaryInteractionNotificationPublishService.publishAfterCommit(DiaryInteractionNotifyCommand.builder()
                    .type(NotificationType.LIKE)
                    .triggerUserId(userId)
                    .recipientUserId(diary.getUserId())
                    .relatedDiaryId(diaryId)
                    .build());

            // 7.更新缓存
            try {
                cacheCounterSupport.incrementDiaryLikeCount(diaryId, 1);
            } catch (Exception ex) {
                log.warn("更新日记点赞缓存失败，diaryId={}, userId={}", diaryId, userId, ex);
            }
            log.info("点赞日记成功，diaryId={}, userId={}", diaryId, userId);
        } else {
            log.info("重复点赞，直接返回当前状态，diaryId={}, userId={}", diaryId, userId);
        }

        return buildLikeVO(true, existed == null ? diary.getLikeCount() + 1 : diary.getLikeCount());
    }

    /**
     * 取消点赞旅行日记
     * @param diaryId 旅行日记 id
     * @param userId 用户 id
     * @return 点赞状态与数量
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public DiaryLikeVO unlikeDiary(Long diaryId, Long userId) {
        // 1.校验参数是否存在
        TravelDiary diary = diaryCheckService.requirePublicDiary(diaryId);
        userCheckService.requireActiveUser(userId);

        // 2.判断是否已经点过赞
        DiaryLike existed = diaryLikeMapper.selectByDiaryIdAndUserId(diaryId, userId);
        if (existed != null) {
            // 3.若点过赞，则删除点赞记录，并更新点赞量
            diaryLikeMapper.deleteByDiaryIdAndUserId(diaryId, userId);
            diaryMapper.updateLikeCount(diaryId, -1);

            // 4.清除Redis中可能受到影响的缓存
            // 4.1.清除日记详情缓存

            // 5.更新缓存
            try {
                cacheCounterSupport.incrementDiaryLikeCount(diaryId, -1);
            } catch (Exception ex) {
                log.warn("更新日记点赞缓存失败，diaryId={}, userId={}", diaryId, userId, ex);
            }
            log.info("取消点赞成功，diaryId={}, userId={}", diaryId, userId);
        } else {
            log.info("用户本次取消点赞时为未点赞状态，直接返回当前状态，diaryId={}, userId={}", diaryId, userId);
        }

        return buildLikeVO(false, existed != null ? diary.getLikeCount() - 1 : diary.getLikeCount());
    }

    /**
     * 构建返回值
     * @param liked 是否点赞
     * @param likeCount 旅行日记点赞数量
     * @return 返回实体
     */
    private DiaryLikeVO buildLikeVO(boolean liked, Integer likeCount) {
        DiaryLikeVO response = new DiaryLikeVO();
        response.setLiked(liked);
        response.setLikeCount(likeCount == null ? 0 : likeCount);
        return response;
    }
}
