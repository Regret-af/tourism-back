package com.af.tourism.service.impl.app;

import com.af.tourism.mapper.DiaryLikeMapper;
import com.af.tourism.mapper.DiaryMapper;
import com.af.tourism.pojo.entity.DiaryLike;
import com.af.tourism.pojo.entity.TravelDiary;
import com.af.tourism.pojo.vo.app.DiaryLikeVO;
import com.af.tourism.service.app.DiaryLikeService;
import com.af.tourism.service.helper.DiaryCheckService;
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

    private final UserCheckService userCheckService;
    private final DiaryCheckService diaryCheckService;

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
            diaryMapper.updateLikeCount(diaryId, 1);
            diary = diaryMapper.selectById(diaryId);
            log.info("点赞日记成功，diaryId={}, userId={}", diaryId, userId);
        } else {
            log.info("重复点赞，直接返回当前状态，diaryId={}, userId={}", diaryId, userId);
        }

        return buildLikeVO(true, diary.getLikeCount());
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
            diary = diaryMapper.selectById(diaryId);
            log.info("取消点赞成功，diaryId={}, userId={}", diaryId, userId);
        } else {
            log.info("用户本次取消点赞时为未点赞状态，直接返回当前状态，diaryId={}, userId={}", diaryId, userId);
        }

        return buildLikeVO(false, diary.getLikeCount());
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
