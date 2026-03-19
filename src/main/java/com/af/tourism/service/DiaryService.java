package com.af.tourism.service;

import com.af.tourism.pojo.dto.DiaryQueryDTO;
import com.af.tourism.pojo.dto.TravelDiaryPublishDTO;
import com.af.tourism.pojo.vo.DiaryCardVO;
import com.af.tourism.pojo.vo.DiaryDetailVO;
import com.af.tourism.pojo.vo.PageResponse;
import com.af.tourism.pojo.vo.TravelDiaryPublishVO;

public interface DiaryService {

    /**
     * 发布旅行日记
     * @param request 旅行日记信息
     * @return 返回值
     */
    TravelDiaryPublishVO publishDiary(TravelDiaryPublishDTO request, Long userId);

    /**
     * 旅行日记列表
     * @param queryDTO 请求参数
     * @return 日记列表
     */
    PageResponse<DiaryCardVO> listDiaries(DiaryQueryDTO queryDTO);

    /**
     * 旅行日记详情
     * @param diaryId 旅行日记 id
     * @return 旅行日记详细信息
     */
    DiaryDetailVO getDiaryDetail(Long diaryId);
}
