package com.af.tourism.service;

import com.af.tourism.pojo.dto.app.AttractionQueryDTO;
import com.af.tourism.pojo.vo.app.AttractionCardVO;
import com.af.tourism.pojo.vo.app.AttractionDetailVO;
import com.af.tourism.pojo.vo.app.AttractionWeatherVO;
import com.af.tourism.pojo.vo.common.PageResponse;

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

    /**
     * 查询景点天气信息、未来天气信息与预警信息
     * @param attractionId 景点 id
     * @return 景点天气信息
     */
    AttractionWeatherVO getAttractionWeather(Long attractionId);
}
