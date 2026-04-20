package com.af.tourism.service.impl.app;

import com.af.tourism.common.ErrorCode;
import com.af.tourism.common.constants.RedisTtlConstants;
import com.af.tourism.common.enums.DiaryDeletedStatus;
import com.af.tourism.common.enums.DiaryVisibility;
import com.af.tourism.converter.DiaryConverter;
import com.af.tourism.exception.BusinessException;
import com.af.tourism.mapper.DiaryCategoryMapper;
import com.af.tourism.mapper.DiaryMapper;
import com.af.tourism.pojo.dto.app.DiaryQueryDTO;
import com.af.tourism.pojo.dto.app.TravelDiaryPublishDTO;
import com.af.tourism.pojo.dto.app.TravelDiaryUpdateDTO;
import com.af.tourism.pojo.entity.TravelDiary;
import com.af.tourism.pojo.vo.app.DiaryCardVO;
import com.af.tourism.pojo.vo.app.DiaryDetailVO;
import com.af.tourism.pojo.vo.app.DiaryInteractStatusVO;
import com.af.tourism.pojo.vo.app.DiaryProfileCardVO;
import com.af.tourism.pojo.vo.app.MyDiaryDetailVO;
import com.af.tourism.pojo.vo.app.MyDiaryProfileCardVO;
import com.af.tourism.pojo.vo.app.TravelDiaryPublishVO;
import com.af.tourism.pojo.vo.common.PageResponse;
import com.af.tourism.security.util.SecurityUtils;
import com.af.tourism.service.app.DiaryService;
import com.af.tourism.service.cache.CacheClient;
import com.af.tourism.service.cache.CacheClearSupport;
import com.af.tourism.service.cache.CacheCounterSupport;
import com.af.tourism.service.cache.CacheKeySupport;
import com.fasterxml.jackson.core.type.TypeReference;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * 旅行日记服务实现。
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DiaryServiceImpl implements DiaryService {

    private static final TypeReference<PageResponse<DiaryCardVO>> DIARY_LIST_PAGE_TYPE =
            new TypeReference<PageResponse<DiaryCardVO>>() {
            };
    private static final TypeReference<PageResponse<MyDiaryProfileCardVO>> MY_DIARY_LIST_PAGE_TYPE =
            new TypeReference<PageResponse<MyDiaryProfileCardVO>>() {
            };
    private static final TypeReference<PageResponse<DiaryProfileCardVO>> USER_PUBLIC_DIARY_LIST_PAGE_TYPE =
            new TypeReference<PageResponse<DiaryProfileCardVO>>() {
            };

    private final DiaryMapper diaryMapper;
    private final DiaryCategoryMapper diaryCategoryMapper;

    private final DiaryConverter diaryConverter;

    private final CacheClient cacheClient;
    private final CacheKeySupport cacheKeySupport;
    private final CacheClearSupport cacheClearSupport;
    private final CacheCounterSupport cacheCounterSupport;

    /**
     * 发布旅行日记
     * @param request 旅行日记信息
     * @return 返回值
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public TravelDiaryPublishVO publishDiary(TravelDiaryPublishDTO request, Long userId) {
        // 1.校验参数
        if (diaryCategoryMapper.selectById(request.getContentType()) == null) {
            throw new BusinessException(ErrorCode.PARAM_INVALID, "日记类型错误");
        }
        if (request.getVisibility() != null && DiaryVisibility.fromValue(request.getVisibility()) == null) {
            throw new BusinessException(ErrorCode.PARAM_INVALID, "日记可见性参数错误");
        }

        // 2.将信息解析到实体
        TravelDiary diary = diaryConverter.toTravelDiary(request);

        // 3.填充发布旅行日记时的业务默认值
        fillPublishDefault(diary, userId);

        // 4.执行插入操作
        int rows = diaryMapper.insert(diary);
        if (rows <= 0) {
            log.error("旅行日记发布失败，数据库插入失败，userId={}", userId);
            throw new BusinessException(ErrorCode.INTERNAL_ERROR, "数据库插入失败");
        }
        log.info("旅行日记发布成功，diaryId={}, userId={}", diary.getId(), userId);

        // 5.如果是公开日记，清除公开相关缓存
        if (isPublicDiary(diary.getStatus(), diary.getVisibility(), diary.getIsDeleted())) {
            cacheClearSupport.clearDiaryList();
            cacheClearSupport.clearUserPublicDiaryList(userId);
            cacheClearSupport.clearMoreFromAuthor(userId);
        }
        cacheClearSupport.clearMyDiaryList(userId);

        return diaryConverter.toTravelDiaryPublishVO(diary);
    }

    /**
     * 编辑旅行日记
     * @param diaryId 日记 id
     * @param request 编辑内容
     * @param userId 用户 id
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateDiary(Long diaryId, TravelDiaryUpdateDTO request, Long userId) {
        // 1.校验参数与权限
        TravelDiary diary = diaryMapper.selectById(diaryId);
        if (diary == null) {
            log.warn("编辑旅行日记失败，日记不存在，diaryId={}, userId={}", diaryId, userId);
            throw new BusinessException(ErrorCode.NOT_FOUND, "旅行日记不存在");
        }
        if (!Objects.equals(diary.getUserId(), userId)) {
            log.warn("编辑旅行日记失败，非作者无权限，diaryId={}, userId={}, authorId={}",
                    diaryId, userId, diary.getUserId());
            throw new BusinessException(ErrorCode.FORBIDDEN, "无权限编辑该日记");
        }
        if (Objects.equals(diary.getIsDeleted(), DiaryDeletedStatus.DELETED.getValue())) {
            log.warn("编辑旅行日记失败，日记已删除，diaryId={}, userId={}", diaryId, userId);
            throw new BusinessException(ErrorCode.NOT_FOUND, "旅行日记不存在");
        }

        boolean wasPublicDiary = isPublicDiary(diary.getStatus(), diary.getVisibility(), diary.getIsDeleted());

        // 2.更新新数据
        diary.setTitle(request.getTitle());
        diary.setSummary(request.getSummary());
        diary.setCoverUrl(request.getCoverUrl());
        diary.setContent(request.getContent());
        if (request.getContentType() != null) {
            if (diaryCategoryMapper.selectById(request.getContentType()) == null) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "日记类型参数错误");
            }
            diary.setContentType(request.getContentType());
        }
        if (request.getVisibility() != null) {
            if (DiaryVisibility.fromValue(request.getVisibility()) == null) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "日记可见性参数错误");
            }
            diary.setVisibility(request.getVisibility());
        }

        // 3.进行更新
        int rows = diaryMapper.updateById(diary);
        if (rows <= 0) {
            log.error("编辑旅行日记失败，数据库更新失败，diaryId={}, userId={}", diaryId, userId);
            throw new BusinessException(ErrorCode.INTERNAL_ERROR, "数据库更新失败");
        }

        // 4.清除可能受影响的缓存
        boolean publicDiary = isPublicDiary(diary.getStatus(), diary.getVisibility(), diary.getIsDeleted());
        if (publicDiary) {
            cacheClearSupport.clearDiaryList();
            cacheClearSupport.clearUserPublicDiaryList(userId);
            cacheClearSupport.clearMoreFromAuthor(userId);
        }
        if (wasPublicDiary || publicDiary) {
            cacheClearSupport.clearDiaryDetail(diaryId);
        }
        cacheClearSupport.clearMyDiaryList(userId);
    }

    /**
     * 删除旅行日记
     * @param diaryId 日记 id
     * @param userId 用户 id
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteDiary(Long diaryId, Long userId) {
        // 1.获取日记实体，并进行校验
        TravelDiary diary = diaryMapper.selectById(diaryId);
        if (diary == null) {
            log.warn("删除旅行日记失败，日记不存在，diaryId={}, userId={}", diaryId, userId);
            throw new BusinessException(ErrorCode.NOT_FOUND, "旅行日记不存在");
        }
        if (!Objects.equals(diary.getUserId(), userId)) {
            log.warn("删除旅行日记失败，非作者无权限，diaryId={}, userId={}, authorId={}",
                    diaryId, userId, diary.getUserId());
            throw new BusinessException(ErrorCode.FORBIDDEN, "无权限删除该日记");
        }
        if (Objects.equals(diary.getIsDeleted(), DiaryDeletedStatus.DELETED.getValue())) {
            log.warn("删除旅行日记失败，日记已删除，diaryId={}, userId={}", diaryId, userId);
            throw new BusinessException(ErrorCode.NOT_FOUND, "旅行日记不存在");
        }

        boolean wasPublicDiary = isPublicDiary(diary.getStatus(), diary.getVisibility(), diary.getIsDeleted());

        // 2.删除旅行日记
        diary.setIsDeleted(DiaryDeletedStatus.DELETED.getValue());
        int rows = diaryMapper.updateById(diary);
        if (rows <= 0) {
            log.error("删除旅行日记失败，数据库更新失败，diaryId={}, userId={}", diaryId, userId);
            throw new BusinessException(ErrorCode.INTERNAL_ERROR, "数据库更新失败");
        }

        // 3.清除 Redis 中可能受到影响的缓存
        if (wasPublicDiary) {
            cacheClearSupport.clearDiaryList();
            cacheClearSupport.clearUserPublicDiaryList(userId);
            cacheClearSupport.clearMoreFromAuthor(userId);
        }
        cacheClearSupport.clearDiaryDetail(diaryId);
        cacheClearSupport.clearMyDiaryList(userId);
    }

    /**
     * 查询我自己的单篇日记详情
     * @param diaryId 日记 id
     * @param userId 当前用户 id
     * @return 我的日记详情
     */
    @Override
    public MyDiaryDetailVO getMyDiaryDetail(Long diaryId, Long userId) {
        MyDiaryDetailVO detailVO = diaryMapper.selectMyDiaryDetail(diaryId, userId);
        if (detailVO == null) {
            log.warn("查询我的日记详情失败，日记不存在或无权限，diaryId={}, userId={}", diaryId, userId);
            throw new BusinessException(ErrorCode.NOT_FOUND, "旅行日记不存在");
        }
        return detailVO;
    }

    /**
     * 旅行日记列表
     * @param queryDTO 请求参数
     * @return 日记列表
     */
    @Override
    public PageResponse<DiaryCardVO> listDiaries(DiaryQueryDTO queryDTO) {
        // 1.获取用户 id
        Long userId = SecurityUtils.getCurrentUserId();

        // 2.构建 Redis 中日记列表的 key
        String cacheKey = cacheKeySupport.buildDiaryListKey(queryDTO);

        // 3.查找 Redis 缓存，存在直接返回
        try {
            PageResponse<DiaryCardVO> cachedResponse = cacheClient.get(cacheKey, DIARY_LIST_PAGE_TYPE);
            if (cachedResponse != null) {
                PageHelper.clearPage();
                return fillDiaryCardInteractStatus(cachedResponse, userId);
            }
        } catch (Exception ex) {
            log.warn("读取日记列表缓存失败，回源数据库，cacheKey={}", cacheKey, ex);
        }

        // 4.开启分页查询，在数据库中进行查找
        PageHelper.startPage(queryDTO.getPageNum(), queryDTO.getPageSize());
        List<DiaryCardVO> list = diaryMapper.selectDiaryList(queryDTO, null);
        PageInfo<DiaryCardVO> pageInfo = new PageInfo<>(list);

        // 5.填充返回值
        PageResponse<DiaryCardVO> response = new PageResponse<>();
        response.setList(list);
        response.setPageNum(pageInfo.getPageNum());
        response.setPageSize(pageInfo.getPageSize());
        response.setTotal(pageInfo.getTotal());

        // 6.将返回值存入 Redis
        try {
            cacheClient.set(cacheKey, response, RedisTtlConstants.DIARY_LIST);
        } catch (Exception ex) {
            log.warn("写入日记列表缓存失败，cacheKey={}", cacheKey, ex);
        }

        return fillDiaryCardInteractStatus(response, userId);
    }

    /**
     * 旅行日记详情
     * @param diaryId 旅行日记 id
     * @return 旅行日记详细信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public DiaryDetailVO getDiaryDetail(Long diaryId) {
        diaryMapper.increaseViewCount(diaryId);

        // 1.获取用户 id
        Long userId = SecurityUtils.getCurrentUserId();

        // 2.构建 Redis 中日记详情的 key
        String cacheKey = cacheKeySupport.buildDiaryDetailKey(diaryId, null);

        // 3.查找 Redis 缓存，存在直接返回
        try {
            // 3.1.查询日记详情缓存
            DiaryDetailVO cachedDetail = cacheClient.get(cacheKey, DiaryDetailVO.class);
            if (cachedDetail != null) {
                // 3.2.增加浏览量，进行回写
                cachedDetail.setViewCount((cachedDetail.getViewCount() == null ? 0 : cachedDetail.getViewCount()) + 1);
                cacheClient.set(cacheKey, cachedDetail, RedisTtlConstants.DEFAULT);
                cacheCounterSupport.syncDiaryViewCount(diaryId, cachedDetail.getViewCount());
                // 3.3.查找用户交互状态信息
                DiaryDetailVO detailVO = mergeDiaryDetailWithInteractStatus(cachedDetail, diaryId, userId);
                // 3.4.填充日记数据统计信息
                cacheCounterSupport.fillDiaryCounters(detailVO, diaryId);
                return detailVO;
            }
        } catch (Exception ex) {
            log.warn("读取日记详情缓存失败，回源数据库，cacheKey={}", cacheKey, ex);
        }

        // 4.在数据库中查询日记详情
        DiaryDetailVO detailVO = diaryMapper.selectDiaryBaseDetail(diaryId);

        // 5.若为空，抛出异常
        if (detailVO == null) {
            log.warn("旅行日记不存在，diaryId={}", diaryId);
            throw new BusinessException(ErrorCode.NOT_FOUND, "旅行日记不存在");
        }

        // 6.将返回值存入 Redis
        try {
            cacheClient.set(cacheKey, detailVO, RedisTtlConstants.DEFAULT);
        } catch (Exception ex) {
            log.warn("写入日记详情缓存失败，cacheKey={}", cacheKey, ex);
        }

        cacheCounterSupport.syncDiaryCounters(diaryId, detailVO.getViewCount(), detailVO.getLikeCount(),
                detailVO.getFavoriteCount(), detailVO.getCommentCount());
        DiaryDetailVO mergedDetailVO = mergeDiaryDetailWithInteractStatus(detailVO, diaryId, userId);
        cacheCounterSupport.fillDiaryCounters(mergedDetailVO, diaryId);
        return mergedDetailVO;
    }

    /**
     * 获取我的日记
     * @param userId 当前用户 id
     * @param queryDTO 排序分页参数
     * @return 分页后的当前用户日记列表
     */
    @Override
    public PageResponse<MyDiaryProfileCardVO> listMyDiaries(Long userId, DiaryQueryDTO queryDTO) {
        // 1.判断查询的是否是已登录用户日记，防止信息泄露
        if (!Objects.equals(userId, SecurityUtils.requireCurrentUserId())) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "暂无权限查询他人日记");
        }

        // 2.构建 Redis 中我的日记列表 key
        String cacheKey = cacheKeySupport.buildMyDiaryListKey(userId, queryDTO);
        try {
            PageResponse<MyDiaryProfileCardVO> cachedResponse = cacheClient.get(cacheKey, MY_DIARY_LIST_PAGE_TYPE);
            if (cachedResponse != null) {
                PageHelper.clearPage();
                return fillMyDiaryProfileCardInteractStatus(cachedResponse, userId);
            }
        } catch (Exception ex) {
            log.warn("读取我的日记列表缓存失败，回源数据库，cacheKey={}", cacheKey, ex);
        }

        // 3.开启分页查询
        PageHelper.startPage(queryDTO.getPageNum(), queryDTO.getPageSize());

        // 4.进行查询操作
        List<MyDiaryProfileCardVO> list = diaryMapper.selectMyDiaryProfileList(userId, queryDTO, null);
        PageInfo<MyDiaryProfileCardVO> pageInfo = new PageInfo<>(list);

        // 5.填充返回值
        PageResponse<MyDiaryProfileCardVO> response = new PageResponse<>();
        response.setList(list);
        response.setPageNum(pageInfo.getPageNum());
        response.setPageSize(pageInfo.getPageSize());
        response.setTotal(pageInfo.getTotal());

        // 6.将返回值存入Redis
        try {
            cacheClient.set(cacheKey, response, RedisTtlConstants.DIARY_LIST);
        } catch (Exception ex) {
            log.warn("写入我的日记列表缓存失败，cacheKey={}", cacheKey, ex);
        }

        return fillMyDiaryProfileCardInteractStatus(response, userId);
    }

    /**
     * 获取他人主页日记
     * @param userId 用户 id
     * @param queryDTO 排序分页参数
     * @return 分页后的用户日记列表
     */
    @Override
    public PageResponse<DiaryProfileCardVO> listUserPublicDiaries(Long userId, DiaryQueryDTO queryDTO) {
        // 1.获取当前用户 id
        Long currentUserId = SecurityUtils.getCurrentUserId();

        // 2.构建 Redis 中用户公开日记列表的 key
        String cacheKey = cacheKeySupport.buildUserPublicDiaryListKey(userId, queryDTO, currentUserId);

        // 3.查找 Redis 缓存，存在直接返回
        try {
            PageResponse<DiaryProfileCardVO> cachedResponse = cacheClient.get(cacheKey, USER_PUBLIC_DIARY_LIST_PAGE_TYPE);
            if (cachedResponse != null) {
                PageHelper.clearPage();
                return fillDiaryProfileCardInteractStatus(cachedResponse, currentUserId);
            }
        } catch (Exception ex) {
            log.warn("读取用户主页日记列表缓存失败，回源数据库，cacheKey={}", cacheKey, ex);
        }

        // 4.开启分页查询
        PageHelper.startPage(queryDTO.getPageNum(), queryDTO.getPageSize());

        // 5.进行查询操作
        List<DiaryProfileCardVO> list = diaryMapper.selectUserDiaryProfileList(userId, queryDTO, null);
        PageInfo<DiaryProfileCardVO> pageInfo = new PageInfo<>(list);

        // 6.填充返回值
        PageResponse<DiaryProfileCardVO> response = new PageResponse<>();
        response.setList(list);
        response.setPageNum(pageInfo.getPageNum());
        response.setPageSize(pageInfo.getPageSize());
        response.setTotal(pageInfo.getTotal());

        // 7.将返回值存入Redis
        try {
            cacheClient.set(cacheKey, response, RedisTtlConstants.DIARY_LIST);
        } catch (Exception ex) {
            log.warn("写入用户主页日记列表缓存失败，cacheKey={}", cacheKey, ex);
        }

        return fillDiaryProfileCardInteractStatus(response, currentUserId);
    }

    /**
     * 查询作者更多创作列表
     * @param diaryId 日记列表
     * @param queryDTO 请求参数
     * @return 作者其他日记列表
     */
    @Override
    public PageResponse<DiaryCardVO> getMoreFromAuthor(Long diaryId, DiaryQueryDTO queryDTO) {
        // 1.根据日记 id 反查出作者
        TravelDiary travelDiary = diaryMapper.selectById(diaryId);
        if (travelDiary == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "旅行日记不存在");
        }

        Long currentUserId = SecurityUtils.getCurrentUserId();

        // 2.构建 Redis 中作者更多创作列表 key
        String cacheKey = cacheKeySupport.buildMoreFromAuthorKey(travelDiary.getUserId(), diaryId, queryDTO, currentUserId);

        // 3.查找 Redis 缓存，存在直接返回
        try {
            PageResponse<DiaryCardVO> cachedResponse = cacheClient.get(cacheKey, DIARY_LIST_PAGE_TYPE);
            if (cachedResponse != null) {
                PageHelper.clearPage();
                return fillDiaryCardInteractStatus(cachedResponse, currentUserId);
            }
        } catch (Exception ex) {
            log.warn("读取作者更多创作缓存失败，回源数据库，cacheKey={}", cacheKey, ex);
        }

        // 4.开启分页查询
        PageHelper.startPage(queryDTO.getPageNum(), queryDTO.getPageSize());

        // 5.进行查询
        List<DiaryCardVO> list = diaryMapper.selectMoreDiariesByAuthor(travelDiary.getUserId(), diaryId, queryDTO, null);
        PageInfo<DiaryCardVO> pageInfo = new PageInfo<>(list);

        // 6.封装返回信息
        PageResponse<DiaryCardVO> response = new PageResponse<>();
        response.setList(list);
        response.setPageNum(pageInfo.getPageNum());
        response.setPageSize(pageInfo.getPageSize());
        response.setTotal(pageInfo.getTotal());

        // 7.将返回值存入Redis
        try {
            cacheClient.set(cacheKey, response, RedisTtlConstants.DIARY_LIST);
        } catch (Exception ex) {
            log.warn("写入作者更多创作缓存失败，cacheKey={}", cacheKey, ex);
        }

        return fillDiaryCardInteractStatus(response, currentUserId);
    }

    /**
     * 查看是否为公开日记
     * @param status 日记状态
     * @param visibility 日记可见性
     * @param isDeleted 是否删除
     * @return 是否公开
     */
    private boolean isPublicDiary(Integer status, Integer visibility, Integer isDeleted) {
        return Objects.equals(status, 1)
                && Objects.equals(visibility, DiaryVisibility.PUBLIC.getValue())
                && Objects.equals(isDeleted, DiaryDeletedStatus.NOT_DELETED.getValue());
    }

    /**
     * 填充旅行日记发布时的默认值
     * @param diary 日记实体
     * @param userId 用户 id
     */
    private void fillPublishDefault(TravelDiary diary, Long userId) {
        diary.setUserId(userId);
        diary.setStatus(1);
        if (diary.getVisibility() == null) {
            diary.setVisibility(DiaryVisibility.PUBLIC.getValue());
        }
        diary.setViewCount(0);
        diary.setLikeCount(0);
        diary.setFavoriteCount(0);
        diary.setCommentCount(0);
        diary.setIsDeleted(0);
        diary.setIsTop(0);
        diary.setPublishedAt(LocalDateTime.now());
    }

    /**
     * 填充日记卡片的交互状态字段
     * @param response 分页返回列表
     * @param userId 查询用户 id
     * @return 填充后的日记列表
     */
    private PageResponse<DiaryCardVO> fillDiaryCardInteractStatus(PageResponse<DiaryCardVO> response, Long userId) {
        // 1.为空直接返回
        if (response == null || response.getList() == null || response.getList().isEmpty()) {
            return response;
        }

        // 2.获取日记 id 列表
        List<Long> diaryIds = extractDiaryIdsFromDiaryCards(response.getList());
        // 3.获取点赞日记 id 集合
        Set<Long> likedDiaryIds = getLikedDiaryIdSet(diaryIds, userId);
        // 4.获取收藏日记 id 集合
        Set<Long> favoritedDiaryIds = getFavoritedDiaryIdSet(diaryIds, userId);

        // 5.进行回写，填充交互状态
        for (DiaryCardVO diaryCardVO : response.getList()) {
            diaryCardVO.setLiked(likedDiaryIds.contains(diaryCardVO.getId()));
            diaryCardVO.setFavorited(favoritedDiaryIds.contains(diaryCardVO.getId()));
        }

        return response;
    }

    /**
     * 填充日记卡片的交互状态字段
     * @param response 分页返回列表
     * @param userId 查询用户 id
     * @return 填充后的日记列表
     */
    private PageResponse<DiaryProfileCardVO> fillDiaryProfileCardInteractStatus(PageResponse<DiaryProfileCardVO> response, Long userId) {
        // 1.为空直接返回
        if (response == null || response.getList() == null || response.getList().isEmpty()) {
            return response;
        }

        // 2.获取日记 id 列表
        List<Long> diaryIds = extractDiaryIdsFromDiaryProfiles(response.getList());
        // 3.获取点赞日记 id 集合
        Set<Long> likedDiaryIds = getLikedDiaryIdSet(diaryIds, userId);
        // 4.获取收藏日记 id 集合
        Set<Long> favoritedDiaryIds = getFavoritedDiaryIdSet(diaryIds, userId);

        // 5.进行回写，填充交互状态
        for (DiaryProfileCardVO diaryProfileCardVO : response.getList()) {
            diaryProfileCardVO.setLiked(likedDiaryIds.contains(diaryProfileCardVO.getId()));
            diaryProfileCardVO.setFavorited(favoritedDiaryIds.contains(diaryProfileCardVO.getId()));
        }
        return response;
    }

    /**
     * 填充日记卡片的交互状态字段
     * @param response 分页返回列表
     * @param userId 查询用户 id
     * @return 填充后的日记列表
     */
    private PageResponse<MyDiaryProfileCardVO> fillMyDiaryProfileCardInteractStatus(PageResponse<MyDiaryProfileCardVO> response, Long userId) {
        // 1.为空直接返回
        if (response == null || response.getList() == null || response.getList().isEmpty()) {
            return response;
        }

        // 2.获取日记 id 列表
        List<Long> diaryIds = extractDiaryIdsFromMyDiaryProfiles(response.getList());
        // 3.获取点赞日记 id 集合
        Set<Long> likedDiaryIds = getLikedDiaryIdSet(diaryIds, userId);
        // 4.获取收藏日记 id 集合
        Set<Long> favoritedDiaryIds = getFavoritedDiaryIdSet(diaryIds, userId);

        // 5.进行回写，填充交互状态
        for (MyDiaryProfileCardVO myDiaryProfileCardVO : response.getList()) {
            myDiaryProfileCardVO.setLiked(likedDiaryIds.contains(myDiaryProfileCardVO.getId()));
            myDiaryProfileCardVO.setFavorited(favoritedDiaryIds.contains(myDiaryProfileCardVO.getId()));
        }
        return response;
    }

    /**
     * 根据日记列表获取日记 id 集合
     * @param list 日记列表
     * @return 日记 id 集合
     */
    private List<Long> extractDiaryIdsFromDiaryCards(List<DiaryCardVO> list) {
        List<Long> diaryIds = new ArrayList<>(list.size());
        for (DiaryCardVO diaryCardVO : list) {
            diaryIds.add(diaryCardVO.getId());
        }
        return diaryIds;
    }

    /**
     * 根据日记列表获取日记 id 集合
     * @param list 日记列表
     * @return 日记 id 集合
     */
    private List<Long> extractDiaryIdsFromDiaryProfiles(List<DiaryProfileCardVO> list) {
        List<Long> diaryIds = new ArrayList<>(list.size());
        for (DiaryProfileCardVO diaryProfileCardVO : list) {
            diaryIds.add(diaryProfileCardVO.getId());
        }
        return diaryIds;
    }

    /**
     * 根据日记列表获取日记 id 集合
     * @param list 日记列表
     * @return 日记 id 集合
     */
    private List<Long> extractDiaryIdsFromMyDiaryProfiles(List<MyDiaryProfileCardVO> list) {
        List<Long> diaryIds = new ArrayList<>(list.size());
        for (MyDiaryProfileCardVO myDiaryProfileCardVO : list) {
            diaryIds.add(myDiaryProfileCardVO.getId());
        }
        return diaryIds;
    }

    /**
     * 获取点赞日记 id 集合
     * @param diaryIds 日记 id 集合
     * @param userId 用户 id
     * @return 点赞日记 id 集合
     */
    private Set<Long> getLikedDiaryIdSet(List<Long> diaryIds, Long userId) {
        if (userId == null || diaryIds == null || diaryIds.isEmpty()) {
            return new HashSet<>();
        }

        Set<Long> likedDiaryIds = diaryMapper.selectLikedDiaryIds(userId, diaryIds);
        return likedDiaryIds == null ? new HashSet<>() : likedDiaryIds;
    }

    /**
     * 获取收藏日记 id 集合
     * @param diaryIds 日记 id 集合
     * @param userId 用户 id
     * @return 收藏日记 id 集合
     */
    private Set<Long> getFavoritedDiaryIdSet(List<Long> diaryIds, Long userId) {
        if (userId == null || diaryIds == null || diaryIds.isEmpty()) {
            return new HashSet<>();
        }

        Set<Long> favoritedDiaryIds = diaryMapper.selectFavoritedDiaryIds(userId, diaryIds);
        return favoritedDiaryIds == null ? new HashSet<>() : favoritedDiaryIds;
    }

    /**
     * 合并日记详情与互动状态
     * @param baseDetailVO 日记详情
     * @param diaryId 日记 id
     * @param userId 用户 id
     * @return 合并后实体
     */
    private DiaryDetailVO mergeDiaryDetailWithInteractStatus(DiaryDetailVO baseDetailVO, Long diaryId, Long userId) {
        DiaryDetailVO detailVO = diaryConverter.copy(baseDetailVO);
        DiaryInteractStatusVO interactStatus = getDiaryInteractStatus(diaryId, userId);
        detailVO.setLiked(interactStatus.getLiked());
        detailVO.setFavorited(interactStatus.getFavorited());
        return detailVO;
    }

    /**
     * 获取日记互动状态
     * @param diaryId 日记 id
     * @param userId 用户 id
     * @return 日记互动状态
     */
    private DiaryInteractStatusVO getDiaryInteractStatus(Long diaryId, Long userId) {
        DiaryInteractStatusVO interactStatus = new DiaryInteractStatusVO();

        // 1.首先置否，默认为空
        interactStatus.setLiked(Boolean.FALSE);
        interactStatus.setFavorited(Boolean.FALSE);
        if (userId == null) {
            return interactStatus;
        }

        // 2.从数据库中获取日记互动状态
        DiaryInteractStatusVO dbInteractStatus = diaryMapper.selectDiaryInteractStatus(diaryId, userId);
        if (dbInteractStatus == null) {
            return interactStatus;
        }

        // 3.查找成功写入实体并返回
        interactStatus.setLiked(Boolean.TRUE.equals(dbInteractStatus.getLiked()));
        interactStatus.setFavorited(Boolean.TRUE.equals(dbInteractStatus.getFavorited()));

        return interactStatus;
    }
}
