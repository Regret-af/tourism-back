package com.af.tourism.service;

import com.af.tourism.pojo.dto.AttractionQueryDTO;
import com.af.tourism.pojo.vo.AttractionCardVO;
import com.af.tourism.pojo.vo.PageResponse;

public interface AttractionService {

    PageResponse<AttractionCardVO> listAttractions(AttractionQueryDTO queryDTO);
}

