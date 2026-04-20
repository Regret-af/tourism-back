package com.af.tourism.service.impl.app;

import com.af.tourism.common.ErrorCode;
import com.af.tourism.common.constants.RedisKeyConstants;
import com.af.tourism.common.constants.RedisTtlConstants;
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
import com.fasterxml.jackson.core.type.TypeReference;
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

    private static final TypeReference<PageResponse<DiaryCommentVO>> DIARY_COMMENT_LIST_PAGE_TYPE =
            new TypeReference<PageResponse<DiaryCommentVO>>() {
            };

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

        // 2.构建 Redis 中日记评论的 key
        String cacheKey = buildDiaryCommentListCacheKey(diaryId, queryDTO);

        // 3.查找 Redis 缓存，存在直接返回
        try {
            PageResponse<DiaryCommentVO> cachedResponse = cacheClient.get(cacheKey, DIARY_COMMENT_LIST_PAGE_TYPE);
            if (cachedResponse != null) {
                return cachedResponse;
            }
        } catch (Exception ex) {
            log.warn("读取日记评论列表缓存失败，回源数据库，cacheKey={}", cacheKey, ex);
        }

        // 4.分页在数据库中查询评论列表
        PageHelper.startPage(queryDTO.getPageNum(), queryDTO.getPageSize());
        List<DiaryCommentVO> list = diaryCommentMapper.selectCommentList(diaryId);
        PageInfo<DiaryCommentVO> pageInfo = new PageInfo<>(list);

        // 5.拼接返回数据
        PageResponse<DiaryCommentVO> response = new PageResponse<>();
        response.setList(list);
        response.setPageNum(pageInfo.getPageNum());
        response.setPageSize(pageInfo.getPageSize());
        response.setTotal(pageInfo.getTotal());

        // 6.将返回值存入Redis
        try {
            cacheClient.set(cacheKey, response, RedisTtlConstants.DIARY_COMMENT_LIST);
        } catch (Exception ex) {
            log.warn("写入日记评论列表缓存失败，cacheKey={}", cacheKey, ex);
        }

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

        // 5.清除Redis中可能受到影响的缓存
        // 5.1.清除旅行日记列表缓存
        clearDiaryListCache();
        clearMyDiaryListCache(diary.getUserId());
        clearUserPublicDiaryListCache(diary.getUserId());
        clearMoreFromAuthorCache(diary.getUserId());
        // 5.2.清除日记详情缓存
        clearDiaryDetailCache(diaryId);
        // 5.3.清除日记评论列表缓存
        clearDiaryCommentListCache(diaryId);

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
     * 清除日记详情缓存
     * @param diaryId 日记 id
     */
    private void clearDiaryDetailCache(Long diaryId) {
        String diaryDetailCacheKeyPattern = cacheKeyBuilder.build(
                RedisKeyConstants.DIARY_DETAIL,
                "diaryId", diaryId
        ) + "*";

        try {
            cacheClient.deleteByPattern(diaryDetailCacheKeyPattern);
        } catch (Exception ex) {
            log.warn("删除日记详情缓存失败，cacheKeyPattern={}", diaryDetailCacheKeyPattern, ex);
        }
    }

    /**
     * 构建 Redis 中日记评论的 key
     * @param diaryId 日记 id
     * @param queryDTO 请求参数
     * @return 日记评论列表 key
     */
    private String buildDiaryCommentListCacheKey(Long diaryId, DiaryCommentQueryDTO queryDTO) {
        return cacheKeyBuilder.build(
                RedisKeyConstants.DIARY_COMMENT_LIST,
                "diaryId", diaryId,
                "pageNum", queryDTO.getPageNum(),
                "pageSize", queryDTO.getPageSize()
        );
    }

    /**
     * 清除日记评论列表缓存
     * @param diaryId 日记 id
     */
    private void clearDiaryCommentListCache(Long diaryId) {
        String diaryCommentListCacheKeyPattern = cacheKeyBuilder.build(
                RedisKeyConstants.DIARY_COMMENT_LIST,
                "diaryId", diaryId
        ) + "*";

        try {
            cacheClient.deleteByPattern(diaryCommentListCacheKeyPattern);
        } catch (Exception ex) {
            log.warn("删除日记评论列表缓存失败，cacheKeyPattern={}", diaryCommentListCacheKeyPattern, ex);
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

    /**
     * 清理我的日记列表缓存
     * @param userId 用户 id
     */
    private void clearMyDiaryListCache(Long userId) {
        String myDiaryListCacheKeyPattern = cacheKeyBuilder.build(
                RedisKeyConstants.DIARY_MY_LIST,
                "userId", userId
        ) + "*";

        try {
            cacheClient.deleteByPattern(myDiaryListCacheKeyPattern);
        } catch (Exception ex) {
            log.warn("删除我的日记列表缓存失败，cacheKeyPattern={}", myDiaryListCacheKeyPattern, ex);
        }
    }

    /**
     * 清理用户公开列表缓存
     * @param userId 用户 id
     */
    private void clearUserPublicDiaryListCache(Long userId) {
        String userPublicDiaryListCacheKeyPattern = cacheKeyBuilder.build(
                RedisKeyConstants.DIARY_USER_PUBLIC_LIST,
                "userId", userId
        ) + "*";

        try {
            cacheClient.deleteByPattern(userPublicDiaryListCacheKeyPattern);
        } catch (Exception ex) {
            log.warn("删除用户主页日记列表缓存失败，cacheKeyPattern={}", userPublicDiaryListCacheKeyPattern, ex);
        }
    }

    /**
     * 清理更多创作列表缓存
     * @param userId 用户 id
     */
    private void clearMoreFromAuthorCache(Long userId) {
        String moreFromAuthorCacheKeyPattern = cacheKeyBuilder.build(
                RedisKeyConstants.DIARY_MORE_FROM_AUTHOR,
                "userId", userId
        ) + "*";

        try {
            cacheClient.deleteByPattern(moreFromAuthorCacheKeyPattern);
        } catch (Exception ex) {
            log.warn("删除作者更多创作缓存失败，cacheKeyPattern={}", moreFromAuthorCacheKeyPattern, ex);
        }
    }
}
