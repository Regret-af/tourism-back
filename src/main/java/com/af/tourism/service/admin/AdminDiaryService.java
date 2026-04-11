package com.af.tourism.service.admin;

import com.af.tourism.pojo.dto.admin.AdminDiaryQueryDTO;
import com.af.tourism.pojo.vo.admin.DiaryForAdminVO;
import com.af.tourism.pojo.vo.common.PageResponse;

import javax.validation.Valid;

/**
 * 管理端日记服务
 */
public interface AdminDiaryService {

    /**
     * 获取管理端日记列表
     *
     * @param queryDTO 查询参数
     * @return 日记分页列表
     */
    PageResponse<DiaryForAdminVO> listDiaries(@Valid AdminDiaryQueryDTO queryDTO);
}
