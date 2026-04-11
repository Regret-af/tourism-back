package com.af.tourism.service.impl.admin;

import com.af.tourism.common.ErrorCode;
import com.af.tourism.common.enums.DiaryStatus;
import com.af.tourism.exception.BusinessException;
import com.af.tourism.mapper.DiaryMapper;
import com.af.tourism.pojo.dto.admin.AdminDiaryQueryDTO;
import com.af.tourism.pojo.entity.TravelDiary;
import com.af.tourism.pojo.vo.admin.DiaryDetailForAdminVO;
import com.af.tourism.pojo.vo.admin.DiaryForAdminVO;
import com.af.tourism.pojo.vo.common.PageResponse;
import com.af.tourism.service.admin.AdminDiaryService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

/**
 * 管理端日记服务实现
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AdminDiaryServiceImpl implements AdminDiaryService {

    private final DiaryMapper diaryMapper;

    /**
     * 获取管理端日记列表
     * @param queryDTO 查询参数
     * @return 日记分页列表
     */
    @Override
    public PageResponse<DiaryForAdminVO> listDiaries(AdminDiaryQueryDTO queryDTO) {
        // 1.开启分页查询
        PageHelper.startPage(queryDTO.getPageNum(), queryDTO.getPageSize());

        // 2.进行查询
        List<DiaryForAdminVO> list = diaryMapper.selectAdminDiaryList(queryDTO);
        PageInfo<DiaryForAdminVO> pageInfo = new PageInfo<>(list);

        // 3.封装返回值
        PageResponse<DiaryForAdminVO> response = new PageResponse<>();
        response.setList(list);
        response.setPageNum(pageInfo.getPageNum());
        response.setPageSize(pageInfo.getPageSize());
        response.setTotal(pageInfo.getTotal());

        return response;
    }

    /**
     * 获取管理端日记详情
     * @param id 日记 id
     * @return 日记详情
     */
    @Override
    public DiaryDetailForAdminVO getDiaryDetail(Long id) {
        // 1.获取日记详情
        DiaryDetailForAdminVO detail = diaryMapper.selectAdminDiaryDetail(id);
        if (detail == null) {
            log.warn("管理端查询日记详情不存在，diaryId={}", id);
            throw new BusinessException(ErrorCode.NOT_FOUND, "日记不存在");
        }

        return detail;
    }

    /**
     * 修改日记状态
     * @param id 日记 id
     * @param status 目标状态
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateDiaryStatus(Long id, Integer status) {
        // 1.获取日记实体并进行非空校验
        TravelDiary diary = diaryMapper.selectById(id);
        if (diary == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "日记不存在");
        }

        // 2.校验状态值的合法性
        if (!DiaryStatus.isValid(status)) {
            throw new BusinessException(ErrorCode.PARAM_INVALID, "日记状态不合法");
        }

        // 3.进行幂等性校验
        if (Objects.equals(diary.getStatus(), status)) {
            return;
        }

        // 4.进行状态修改
        diary.setStatus(status);
        diaryMapper.updateById(diary);
    }

    /**
     * 修改日记逻辑删除状态
     * @param id 日记 id
     * @param isDeleted 目标逻辑删除状态
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateDiaryDeleted(Long id, Integer isDeleted) {
        // 1.获取日记实体并进行非空校验
        TravelDiary diary = diaryMapper.selectById(id);
        if (diary == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "日记不存在");
        }

        // 2.校验逻辑删除状态值的合法性
        if (isDeleted == null || (isDeleted != 0 && isDeleted != 1)) {
            throw new BusinessException(ErrorCode.PARAM_INVALID, "日记逻辑删除状态不合法");
        }

        // 3.进行幂等性校验
        if (Objects.equals(diary.getIsDeleted(), isDeleted)) {
            return;
        }

        // 4.进行逻辑删除状态修改
        diary.setIsDeleted(isDeleted);
        diaryMapper.updateById(diary);
    }
}
