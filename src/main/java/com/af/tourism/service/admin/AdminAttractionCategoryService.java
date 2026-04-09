package com.af.tourism.service.admin;

import com.af.tourism.pojo.dto.admin.AttractionCategoryCreateDTO;
import com.af.tourism.pojo.dto.admin.AttractionCategoryQueryDTO;
import com.af.tourism.pojo.vo.admin.AttractionCategoryForAdminVO;
import com.af.tourism.pojo.vo.common.PageResponse;

import javax.validation.Valid;

/**
 * 管理端景点分类服务
 */
public interface AdminAttractionCategoryService {

    /**
     * 获取管理端景点分类列表
     * @param queryDTO 查询参数
     * @return 分类分页列表
     */
    PageResponse<AttractionCategoryForAdminVO> listCategories(@Valid AttractionCategoryQueryDTO queryDTO);

    /**
     * 获取管理端景点分类详情
     * @param id 分类 id
     * @return 分类详情
     */
    AttractionCategoryForAdminVO getCategoryDetail(Long id);

    /**
     * 新增景点分类
     * @param request 新增请求
     * @return 新增后的分类详情
     */
    AttractionCategoryForAdminVO createCategory(@Valid AttractionCategoryCreateDTO request);
}
