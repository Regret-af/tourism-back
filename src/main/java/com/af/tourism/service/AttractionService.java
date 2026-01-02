package com.af.tourism.service;

import com.af.tourism.pojo.vo.AttractionCardVO;
import com.af.tourism.pojo.vo.PageResponse;

public interface AttractionService {

    PageResponse<AttractionCardVO> listAttractions(Integer page, Integer size, String q, String location, Integer priceLevel, String tags, String sort);
}

