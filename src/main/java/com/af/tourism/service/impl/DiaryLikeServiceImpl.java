package com.af.tourism.service.impl;

import com.af.tourism.common.ErrorCode;
import com.af.tourism.exception.BusinessException;
import com.af.tourism.mapper.DiaryLikeMapper;
import com.af.tourism.mapper.DiaryMapper;
import com.af.tourism.mapper.UserMapper;
import com.af.tourism.pojo.entity.DiaryLike;
import com.af.tourism.pojo.entity.TravelDiary;
import com.af.tourism.pojo.vo.DiaryLikeVO;
import com.af.tourism.service.DiaryLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 点赞服务实现。
 */
@Service
@RequiredArgsConstructor
public class DiaryLikeServiceImpl implements DiaryLikeService {

    private final DiaryLikeMapper diaryLikeMapper;
    private final DiaryMapper diaryMapper;
    private final UserMapper userMapper;

    private final UserCheckService userCheckService;

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
        TravelDiary diary = requirePublicDiary(diaryId);
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
        TravelDiary diary = requirePublicDiary(diaryId);
        userCheckService.requireActiveUser(userId);

        // 2.判断是否已经点过赞
        DiaryLike existed = diaryLikeMapper.selectByDiaryIdAndUserId(diaryId, userId);
        if (existed != null) {
            // 3.若点过赞，则删除点赞记录，并更新点赞量
            diaryLikeMapper.deleteByDiaryIdAndUserId(diaryId, userId);
            diaryMapper.updateLikeCount(diaryId, -1);
            diary = diaryMapper.selectById(diaryId);
        }

        return buildLikeVO(false, diary.getLikeCount());
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
