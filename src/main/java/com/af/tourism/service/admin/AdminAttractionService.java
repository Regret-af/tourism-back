package com.af.tourism.service.admin;

import com.af.tourism.pojo.dto.admin.AdminAttractionCreateDTO;
import com.af.tourism.pojo.dto.admin.AdminAttractionQueryDTO;
import com.af.tourism.pojo.vo.admin.AttractionDetailForAdminVO;
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

    /**
     * 管理端景点详情
     * @param id 景点 id
     * @return 景点详情信息
     */
    AttractionDetailForAdminVO getAttractionDetail(Long id);

    /**
     * 管理端新增景点
     * @param request 请求信息
     * @return 新增后的景点详情
     */
    AttractionDetailForAdminVO createAttraction(@Valid AdminAttractionCreateDTO request);
}
