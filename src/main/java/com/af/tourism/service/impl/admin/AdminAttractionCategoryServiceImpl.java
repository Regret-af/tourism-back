package com.af.tourism.service.impl.admin;

import com.af.tourism.mapper.AttractionCategoryMapper;
import com.af.tourism.pojo.dto.admin.AttractionCategoryQueryDTO;
import com.af.tourism.pojo.vo.admin.AttractionCategoryForAdminVO;
import com.af.tourism.pojo.vo.common.PageResponse;
import com.af.tourism.service.admin.AdminAttractionCategoryService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 管理端景点分类服务实现
 */
@Service
@RequiredArgsConstructor
public class AdminAttractionCategoryServiceImpl implements AdminAttractionCategoryService {

    private final AttractionCategoryMapper attractionCategoryMapper;

    /**
     * 获取管理端景点分类列表
     * @param queryDTO 查询参数
     * @return 分类分页列表
     */
    @Override
    public PageResponse<AttractionCategoryForAdminVO> listCategories(AttractionCategoryQueryDTO queryDTO) {
        // 1.开启分页查询
        PageHelper.startPage(queryDTO.getPageNum(), queryDTO.getPageSize());

        // 2.进行查询
        List<AttractionCategoryForAdminVO> list = attractionCategoryMapper.selectAdminCategoryList(queryDTO);
        PageInfo<AttractionCategoryForAdminVO> pageInfo = new PageInfo<>(list);

        // 3.封装返回值
        PageResponse<AttractionCategoryForAdminVO> response = new PageResponse<>();
        response.setList(list);
        response.setPageNum(pageInfo.getPageNum());
        response.setPageSize(pageInfo.getPageSize());
        response.setTotal(pageInfo.getTotal());

        return response;
    }
}
