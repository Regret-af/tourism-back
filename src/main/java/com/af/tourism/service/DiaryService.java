package com.af.tourism.service;

import com.af.tourism.pojo.dto.DiaryQueryDTO;
import com.af.tourism.pojo.vo.DiaryCardVO;
import com.af.tourism.pojo.vo.PageResponse;

public interface DiaryService {

    PageResponse<DiaryCardVO> listDiaries(DiaryQueryDTO queryDTO);
}

