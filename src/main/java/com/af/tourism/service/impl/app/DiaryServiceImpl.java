package com.af.tourism.service.impl.app;

import com.af.tourism.common.ErrorCode;
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
import com.af.tourism.pojo.vo.app.DiaryProfileCardVO;
import com.af.tourism.pojo.vo.app.MyDiaryDetailVO;
import com.af.tourism.pojo.vo.app.MyDiaryProfileCardVO;
import com.af.tourism.pojo.vo.app.TravelDiaryPublishVO;
import com.af.tourism.pojo.vo.common.PageResponse;
import com.af.tourism.security.SecurityUtils;
import com.af.tourism.service.app.DiaryService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * 旅行日记服务实现。
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DiaryServiceImpl implements DiaryService {

    private final DiaryMapper diaryMapper;
    private final DiaryCategoryMapper diaryCategoryMapper;
    private final DiaryConverter diaryConverter;

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
    }

    /**
     * 删除旅行日记
     * @param diaryId 日记 id
     * @param userId 用户 id
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteDiary(Long diaryId, Long userId) {
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

        diary.setIsDeleted(DiaryDeletedStatus.DELETED.getValue());
        int rows = diaryMapper.updateById(diary);
        if (rows <= 0) {
            log.error("删除旅行日记失败，数据库更新失败，diaryId={}, userId={}", diaryId, userId);
            throw new BusinessException(ErrorCode.INTERNAL_ERROR, "数据库更新失败");
        }
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
        // 1.开启分页查询
        PageHelper.startPage(queryDTO.getPageNum(), queryDTO.getPageSize());

        // 2.进行查询操作
        Long userId = SecurityUtils.getCurrentUserId();
        List<DiaryCardVO> list = diaryMapper.selectDiaryList(queryDTO, userId);
        PageInfo<DiaryCardVO> pageInfo = new PageInfo<>(list);

        // 3.填充返回值
        PageResponse<DiaryCardVO> response = new PageResponse<>();
        response.setList(list);
        response.setPageNum(pageInfo.getPageNum());
        response.setPageSize(pageInfo.getPageSize());
        response.setTotal(pageInfo.getTotal());

        return response;
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
        // 1.查询旅行日记详情
        Long userId = SecurityUtils.getCurrentUserId();
        DiaryDetailVO detailVO = diaryMapper.selectDiaryDetail(diaryId, userId);

        // 2.若为空，抛出异常
        if (detailVO == null) {
            log.warn("旅行日记不存在，diaryId={}", diaryId);
            throw new BusinessException(ErrorCode.NOT_FOUND, "旅行日记不存在");
        }

        return detailVO;
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

        // 2.开启分页查询
        PageHelper.startPage(queryDTO.getPageNum(), queryDTO.getPageSize());

        // 3.进行查询操作
        List<MyDiaryProfileCardVO> list = diaryMapper.selectMyDiaryProfileList(userId, queryDTO, userId);
        PageInfo<MyDiaryProfileCardVO> pageInfo = new PageInfo<>(list);

        // 4.填充返回值
        PageResponse<MyDiaryProfileCardVO> response = new PageResponse<>();
        response.setList(list);
        response.setPageNum(pageInfo.getPageNum());
        response.setPageSize(pageInfo.getPageSize());
        response.setTotal(pageInfo.getTotal());

        return response;
    }

    /**
     * 获取他人主页日记
     * @param userId 用户 id
     * @param queryDTO 排序分页参数
     * @return 分页后的用户日记列表
     */
    @Override
    public PageResponse<DiaryProfileCardVO> listUserPublicDiaries(Long userId, DiaryQueryDTO queryDTO) {
        // 1.开启分页查询
        PageHelper.startPage(queryDTO.getPageNum(), queryDTO.getPageSize());

        // 2.进行查询操作
        Long currentUserId = SecurityUtils.getCurrentUserId();
        List<DiaryProfileCardVO> list = diaryMapper.selectUserDiaryProfileList(userId, queryDTO, currentUserId);
        PageInfo<DiaryProfileCardVO> pageInfo = new PageInfo<>(list);

        // 3.填充返回值
        PageResponse<DiaryProfileCardVO> response = new PageResponse<>();
        response.setList(list);
        response.setPageNum(pageInfo.getPageNum());
        response.setPageSize(pageInfo.getPageSize());
        response.setTotal(pageInfo.getTotal());

        return response;
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

        // 2.开启分页查询
        PageHelper.startPage(queryDTO.getPageNum(), queryDTO.getPageSize());

        // 3.进行查询
        Long userId = SecurityUtils.getCurrentUserId();
        List<DiaryCardVO> list = diaryMapper.selectMoreDiariesByAuthor(travelDiary.getUserId(), diaryId, queryDTO, userId);
        PageInfo<DiaryCardVO> pageInfo = new PageInfo<>(list);

        // 4.封装返回信息
        PageResponse<DiaryCardVO> response = new PageResponse<>();
        response.setList(list);
        response.setPageNum(pageInfo.getPageNum());
        response.setPageSize(pageInfo.getPageSize());
        response.setTotal(pageInfo.getTotal());

        return response;
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
}
