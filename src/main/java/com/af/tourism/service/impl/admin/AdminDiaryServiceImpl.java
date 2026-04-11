package com.af.tourism.service.impl.admin;

import com.af.tourism.common.ErrorCode;
import com.af.tourism.exception.BusinessException;
import com.af.tourism.mapper.DiaryMapper;
import com.af.tourism.pojo.dto.admin.AdminDiaryQueryDTO;
import com.af.tourism.pojo.vo.admin.DiaryDetailForAdminVO;
import com.af.tourism.pojo.vo.admin.DiaryForAdminVO;
import com.af.tourism.pojo.vo.common.PageResponse;
import com.af.tourism.service.admin.AdminDiaryService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

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
     *
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
}
