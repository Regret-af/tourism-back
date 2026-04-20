package com.af.tourism.service.impl.app;

import com.af.tourism.common.ErrorCode;
import com.af.tourism.common.constants.RedisKeyConstants;
import com.af.tourism.common.enums.NotificationType;
import com.af.tourism.converter.DiaryConverter;
import com.af.tourism.converter.UserConverter;
import com.af.tourism.exception.BusinessException;
import com.af.tourism.mapper.DiaryCommentMapper;
import com.af.tourism.mapper.DiaryMapper;
import com.af.tourism.pojo.dto.app.DiaryCommentCreateDTO;
import com.af.tourism.pojo.dto.app.DiaryCommentQueryDTO;
import com.af.tourism.pojo.dto.common.DiaryInteractionNotifyCommand;
import com.af.tourism.pojo.entity.DiaryComment;
import com.af.tourism.pojo.entity.TravelDiary;
import com.af.tourism.pojo.entity.User;
import com.af.tourism.pojo.vo.app.DiaryCommentCreateVO;
import com.af.tourism.pojo.vo.app.DiaryCommentVO;
import com.af.tourism.pojo.vo.common.PageResponse;
import com.af.tourism.service.app.DiaryCommentService;
import com.af.tourism.service.cache.CacheClient;
import com.af.tourism.service.cache.CacheKeyBuilder;
import com.af.tourism.service.helper.DiaryCheckService;
import com.af.tourism.service.helper.DiaryInteractionNotificationService;
import com.af.tourism.service.helper.UserCheckService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 评论服务实现。
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DiaryCommentServiceImpl implements DiaryCommentService {

    private final DiaryCommentMapper diaryCommentMapper;
    private final DiaryMapper diaryMapper;

    private final CacheClient cacheClient;
    private final CacheKeyBuilder cacheKeyBuilder;

    private final UserConverter userConverter;
    private final DiaryConverter diaryConverter;

    private final UserCheckService userCheckService;
    private final DiaryCheckService diaryCheckService;
    private final DiaryInteractionNotificationService diaryInteractionNotificationService;

    /**
     * 查询旅行日记评论列表
     * @param diaryId 旅行日记 id
     * @param queryDTO 旅行日记评论列表查询条件
     * @return 旅行日记评论列表
     */
    @Override
    public PageResponse<DiaryCommentVO> listComments(Long diaryId, DiaryCommentQueryDTO queryDTO) {
        // 1.校验旅行日记是否存在
        diaryCheckService.requirePublicDiary(diaryId);

        // 2.开启分页查询
        PageHelper.startPage(queryDTO.getPageNum(), queryDTO.getPageSize());
        // 3.开始查询评论列表
        log.debug("查询日记评论列表，diaryId={}, pageNum={}, pageSize={}",
                diaryId, queryDTO.getPageNum(), queryDTO.getPageSize());
        List<DiaryCommentVO> list = diaryCommentMapper.selectCommentList(diaryId);
        PageInfo<DiaryCommentVO> pageInfo = new PageInfo<>(list);

        // 4.拼接返回数据
        PageResponse<DiaryCommentVO> response = new PageResponse<>();
        response.setList(list);
        response.setPageNum(pageInfo.getPageNum());
        response.setPageSize(pageInfo.getPageSize());
        response.setTotal(pageInfo.getTotal());

        return response;
    }

    /**
     * 发表评论
     * @param diaryId 旅行日记 id
     * @param userId 评论作者
     * @param request 评论内容
     * @return 评论详情
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public DiaryCommentCreateVO createComment(Long diaryId, Long userId, DiaryCommentCreateDTO request) {
        // 1.校验旅行日记是否存在
        TravelDiary diary = diaryCheckService.requirePublicDiary(diaryId);
        User user = userCheckService.requireActiveUser(userId);

        // 2.填充发布评论时的初始值和业务默认值
        DiaryComment comment = new DiaryComment();
        fillDiaryCommentDefault(comment);
        comment.setDiaryId(diaryId);
        comment.setUserId(userId);
        comment.setContent(request.getContent());

        // 3.执行插入操作
        int rows = diaryCommentMapper.insert(comment);
        if (rows <= 0) {
            log.error("发表评论失败，数据库插入失败");
            throw new BusinessException(ErrorCode.INTERNAL_ERROR, "数据库插入失败");
        }
        // 4.执行旅行日记评论数量更新操作
        diaryMapper.updateCommentCount(diaryId, 1);

        // 5.清除旅行日记列表缓存
        clearDiaryListCache();

        // 6.添加通知列表
        diaryInteractionNotificationService.notifyInteraction(DiaryInteractionNotifyCommand.builder()
                .type(NotificationType.COMMENT)
                .triggerUserId(userId)
                .recipientUserId(diary.getUserId())
                .relatedDiaryId(diaryId)
                .relatedCommentId(comment.getId())
                .build());
        log.info("发表评论成功，diaryId={}, commentId={}, userId={}", diaryId, comment.getId(), userId);

        // 7.返回评论信息
        DiaryCommentCreateVO response = diaryConverter.toDiaryCommentCreateVO(comment);
        response.setAuthor(userConverter.toUserPublicVO(user));

        return response;
    }

    /**
     * 清除旅行日记列表缓存
     */
    private void clearDiaryListCache() {
        String diaryListCacheKeyPattern = cacheKeyBuilder.build(RedisKeyConstants.DIARY_LIST) + "*";

        try {
            cacheClient.deleteByPattern(diaryListCacheKeyPattern);
        } catch (Exception ex) {
            log.warn("删除日记列表缓存失败，cacheKeyPattern={}", diaryListCacheKeyPattern, ex);
        }
    }

    /**
     * 填充评论默认值
     * @param comment 评论实体
     */
    private void fillDiaryCommentDefault(DiaryComment comment) {
        comment.setParentId(null);
        comment.setReplyToUserId(null);
        comment.setStatus(1);
        comment.setCreatedAt(LocalDateTime.now());
        comment.setUpdatedAt(comment.getCreatedAt());
    }
}
