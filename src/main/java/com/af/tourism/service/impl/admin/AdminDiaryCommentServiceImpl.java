package com.af.tourism.service.impl.admin;

import com.af.tourism.common.ErrorCode;
import com.af.tourism.common.constants.RedisKeyConstants;
import com.af.tourism.common.enums.DiaryCommentStatus;
import com.af.tourism.exception.BusinessException;
import com.af.tourism.mapper.DiaryCommentMapper;
import com.af.tourism.mapper.DiaryMapper;
import com.af.tourism.pojo.dto.admin.AdminDiaryCommentQueryDTO;
import com.af.tourism.pojo.entity.DiaryComment;
import com.af.tourism.pojo.vo.admin.DiaryCommentForAdminVO;
import com.af.tourism.pojo.vo.common.PageResponse;
import com.af.tourism.service.admin.AdminDiaryCommentService;
import com.af.tourism.service.cache.CacheClient;
import com.af.tourism.service.cache.CacheKeyBuilder;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

/**
 * 管理端评论服务实现
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AdminDiaryCommentServiceImpl implements AdminDiaryCommentService {

    private final DiaryCommentMapper diaryCommentMapper;
    private final DiaryMapper diaryMapper;
    private final CacheClient cacheClient;
    private final CacheKeyBuilder cacheKeyBuilder;

    /**
     * 获取评论列表
     * @param queryDTO 查询参数
     * @return 评论分页列表
     */
    @Override
    public PageResponse<DiaryCommentForAdminVO> listComments(AdminDiaryCommentQueryDTO queryDTO) {
        // 1.开启分页查询
        PageHelper.startPage(queryDTO.getPageNum(), queryDTO.getPageSize());

        // 2.进行查询
        List<DiaryCommentForAdminVO> list = diaryCommentMapper.selectAdminCommentList(queryDTO);
        PageInfo<DiaryCommentForAdminVO> pageInfo = new PageInfo<>(list);

        // 3.封装返回值
        PageResponse<DiaryCommentForAdminVO> response = new PageResponse<>();
        response.setList(list);
        response.setPageNum(pageInfo.getPageNum());
        response.setPageSize(pageInfo.getPageSize());
        response.setTotal(pageInfo.getTotal());

        return response;
    }

    /**
     * 修改评论状态
     * @param id 评论 id
     * @param status 目标状态
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCommentStatus(Long id, DiaryCommentStatus status) {
        // 1.获取评论实体并进行非空校验
        DiaryComment comment = diaryCommentMapper.selectById(id);
        if (comment == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "评论不存在");
        }

        // 2.校验状态值的合法性
        if (status == null) {
            throw new BusinessException(ErrorCode.PARAM_INVALID, "评论状态不合法");
        }

        // 3.进行幂等性校验
        Integer targetStatus = status.getValue();
        if (Objects.equals(comment.getStatus(), targetStatus)) {
            return;
        }

        // 4.进行状态修改
        Integer oldStatus = comment.getStatus();
        comment.setStatus(targetStatus);
        diaryCommentMapper.updateById(comment);

        // 5.同步维护日记评论数，仅对一级评论生效
        if (comment.getParentId() == null) {
            if (Objects.equals(oldStatus, DiaryCommentStatus.NORMAL.getValue())
                    && Objects.equals(targetStatus, DiaryCommentStatus.HIDDEN.getValue())) {
                diaryMapper.updateCommentCount(comment.getDiaryId(), -1);
            } else if (Objects.equals(oldStatus, DiaryCommentStatus.HIDDEN.getValue())
                    && Objects.equals(targetStatus, DiaryCommentStatus.NORMAL.getValue())) {
                diaryMapper.updateCommentCount(comment.getDiaryId(), 1);
            }
        }

        // 6.清除Redis中可能受到影响的缓存
        // 6.1.清除出日记列表缓存
        clearDiaryListCache();
        // 6.2.清除日记详情缓存
        clearDiaryDetailCache(comment.getDiaryId());
        // 6.3.清除日记评论列表缓存
        clearDiaryCommentListCache(comment.getDiaryId());
    }

    /**
     * 清除出日记列表缓存
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
}
