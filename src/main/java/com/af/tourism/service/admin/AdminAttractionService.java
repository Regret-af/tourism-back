package com.af.tourism.service.admin;

import com.af.tourism.pojo.dto.admin.AdminAttractionQueryDTO;
import com.af.tourism.pojo.vo.admin.AttractionForAdminVO;
import com.af.tourism.pojo.vo.common.PageResponse;

import javax.validation.Valid;

/**
 * 管理端景点服务
 */
public interface AdminAttractionService {

    /**
     * 管理端景点列表
     * @param queryDTO 查询参数
     * @return 景点分页列表
     */
    PageResponse<AttractionForAdminVO> listAttractions(@Valid AdminAttractionQueryDTO queryDTO);
}
