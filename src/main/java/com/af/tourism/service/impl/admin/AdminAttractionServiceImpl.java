package com.af.tourism.service.impl.admin;

import com.af.tourism.mapper.AttractionMapper;
import com.af.tourism.pojo.dto.admin.AdminAttractionQueryDTO;
import com.af.tourism.pojo.vo.admin.AttractionForAdminVO;
import com.af.tourism.pojo.vo.common.PageResponse;
import com.af.tourism.service.admin.AdminAttractionService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 管理端景点服务实现
 */
@Service
@RequiredArgsConstructor
public class AdminAttractionServiceImpl implements AdminAttractionService {

    private final AttractionMapper attractionMapper;

    /**
     * 管理端景点列表
     * @param queryDTO 查询参数
     * @return 景点分页列表
     */
    @Override
    public PageResponse<AttractionForAdminVO> listAttractions(AdminAttractionQueryDTO queryDTO) {
        // 1.开启分页查询
        PageHelper.startPage(queryDTO.getPageNum(), queryDTO.getPageSize());

        // 2.进行景点列表查询
        List<AttractionForAdminVO> list = attractionMapper.selectAdminAttractions(queryDTO);
        PageInfo<AttractionForAdminVO> pageInfo = new PageInfo<>(list);

        // 3.封装返回值
        PageResponse<AttractionForAdminVO> response = new PageResponse<>();
        response.setList(list);
        response.setPageNum(pageInfo.getPageNum());
        response.setPageSize(pageInfo.getPageSize());
        response.setTotal(pageInfo.getTotal());

        return response;
    }
}
