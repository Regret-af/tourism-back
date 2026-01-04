package com.af.tourism.service;

import com.af.tourism.pojo.vo.DiaryCardVO;
import com.af.tourism.pojo.vo.PageResponse;

public interface DiaryService {

    PageResponse<DiaryCardVO> listDiaries(Integer page, Integer size, Long userId, Integer featured, String q, String sort);
}

