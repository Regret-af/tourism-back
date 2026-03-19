package com.af.tourism.service;

import com.af.tourism.pojo.dto.AttractionQueryDTO;
import com.af.tourism.pojo.vo.AttractionCardVO;
import com.af.tourism.pojo.vo.AttractionDetailVO;
import com.af.tourism.pojo.vo.PageResponse;

public interface AttractionService {

    /**
     * 景点列表，覆盖列表、搜索、分类筛选
     * @param queryDTO 列表、搜索、分类筛选参数
     * @return 景点列表
     */
    PageResponse<AttractionCardVO> listAttractions(AttractionQueryDTO queryDTO);

    /**
     * 景点详情
     * @param attractionId 景点id
     * @return 景点详情信息
     */
    AttractionDetailVO getAttractionDetail(Long attractionId);
}
